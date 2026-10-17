# NoteDesu BGM synthesizer: original procedural music, loop-seamless via circular rendering.
import numpy as np, wave, sys
SR = 44100
rng = np.random.default_rng(7)

def midi(n): return 440.0 * 2 ** ((n - 69) / 12)

class Track:
    def __init__(self, seconds):
        self.n = int(seconds * SR); self.buf = np.zeros(self.n)
    def add(self, start, sig):
        # wrap tails around the loop point so the loop is seamless
        idx = (np.arange(len(sig)) + int(start * SR)) % self.n
        np.add.at(self.buf, idx, sig)

def env(n, a, r, sus=1.0):
    t = np.arange(n) / SR; e = np.minimum(1, t / max(a, 1e-4))
    rel = np.clip((n / SR - t) / max(r, 1e-4), 0, 1)
    return e * rel * sus

def bell(freq, dur, amp):
    t = np.arange(int(dur * SR)) / SR
    s = (np.sin(2*np.pi*freq*t) + 0.45*np.sin(2*np.pi*freq*2.76*t)*np.exp(-t*3) + 0.25*np.sin(2*np.pi*freq*5.4*t)*np.exp(-t*6))
    return amp * s * np.exp(-t * 2.2) * np.minimum(1, t / 0.004)

def epiano(freq, dur, amp):
    t = np.arange(int(dur * SR)) / SR
    mod = np.sin(2*np.pi*freq*t) * 1.2 * np.exp(-t*2.5)
    s = np.sin(2*np.pi*freq*t + mod) + 0.2*np.sin(2*np.pi*freq*2*t)*np.exp(-t*4)
    return amp * s * np.exp(-t * 1.6) * np.minimum(1, t / 0.006)

def pad(freqs, dur, amp, bright=0.3):
    t = np.arange(int(dur * SR)) / SR; s = np.zeros_like(t)
    for f in freqs:
        for d in (-0.12, 0.0, 0.11):
            ff = f * 2 ** (d / 12); ph = rng.uniform(0, 2*np.pi)
            s += np.sin(2*np.pi*ff*t + ph) + bright*np.sin(2*np.pi*2*ff*t + ph) + bright*0.4*np.sin(2*np.pi*3*ff*t)
    return amp * s / (3 * len(freqs)) * env(len(t), dur * 0.35, dur * 0.35)

def noise_hit(dur, amp, decay):
    t = np.arange(int(dur * SR)) / SR
    return amp * rng.standard_normal(len(t)) * np.exp(-t * decay)

def thump(freq, dur, amp):
    t = np.arange(int(dur * SR)) / SR
    f = freq * (1 + 1.5 * np.exp(-t * 30))
    return amp * np.sin(2*np.pi*np.cumsum(f)/SR) * np.exp(-t * 9)

def fft_filter(x, lo=None, hi=None):
    X = np.fft.rfft(x); f = np.fft.rfftfreq(len(x), 1/SR); g = np.ones_like(f)
    if hi: g *= 1 / np.sqrt(1 + (f / hi) ** 4)
    if lo: g *= 1 / np.sqrt(1 + (lo / np.maximum(f, 1e-3)) ** 4)
    return np.fft.irfft(X * g, len(x))

def reverb(x, seconds=2.5, mix=0.3):
    n = int(seconds * SR); t = np.arange(n) / SR
    ir = rng.standard_normal(n) * np.exp(-t * 6.9 / seconds); ir[0] = 0
    ir = fft_filter(np.pad(ir, (0, len(x) - n)), hi=5000)[:len(x)]
    wet = np.fft.irfft(np.fft.rfft(x) * np.fft.rfft(ir), len(x))  # circular = loop-safe
    wet *= np.std(x) / (np.std(wet) + 1e-9)
    return (1 - mix) * x + mix * wet

def write(name, x, peak=0.7):
    x = x / (np.max(np.abs(x)) + 1e-9) * peak
    w = wave.open(name, 'wb'); w.setnchannels(1); w.setsampwidth(2); w.setframerate(SR)
    w.writeframes((x * 32767).astype('<i2').tobytes()); w.close()

def daily():
    bpm = 72; beat = 60 / bpm; bars = 16; tr = Track(bars * 4 * beat)
    prog = [(57, [57, 60, 64]), (53, [53, 57, 60]), (48, [55, 60, 64]), (55, [55, 59, 62])]  # Am F C G
    melody = [76, 74, 72, 71, 72, 74, 76, 79, 77, 76, 74, 72, 71, 72, 74, 71]
    for b in range(bars):
        root, ch = prog[b % 4]; t0 = b * 4 * beat
        tr.add(t0, pad([midi(n) for n in ch], 4 * beat * 1.1, 0.25, 0.2))
        tr.add(t0, epiano(midi(root - 12), 3.5, 0.35))
        for i, n in enumerate([ch[0], ch[1], ch[2], ch[1] + 12, ch[2], ch[1], ch[0] + 12, ch[2]]):
            tr.add(t0 + i * beat / 2, epiano(midi(n + 12), 1.6, 0.13))
        if b >= 4 and b % 2 == 0:
            for k in range(2):
                tr.add(t0 + k * 2 * beat + beat * 0.5, bell(midi(melody[(b + k) % 16]), 2.5, 0.12))
    return reverb(fft_filter(tr.buf, lo=40, hi=7000), 2.8, 0.35)

def dark():
    beat = 60 / 54; bars = 14; tr = Track(bars * 4 * beat)
    chords = [[38, 45, 50, 53], [38, 46, 50, 53], [36, 43, 48, 51], [37, 44, 49, 52]]  # Dm, Bb/D, Cm, C#dim-ish
    for b in range(0, bars, 2):
        ch = chords[(b // 2) % 4]; t0 = b * 4 * beat
        tr.add(t0, pad([midi(n) for n in ch], 8 * beat * 1.15, 0.5, 0.45))
        tr.add(t0, pad([midi(ch[0] - 12)], 8 * beat * 1.15, 0.35, 0.1))
    box = [74, 77, 76, 69, 74, 81, 80, 76]
    for b in range(bars):
        if b % 2 == 1:
            for i in range(3):
                tr.add(b * 4 * beat + i * beat * 1.33, bell(midi(box[(b + i) % 8]), 3.0, 0.11))
        tr.add(b * 4 * beat + 3.5 * beat, noise_hit(0.8, 0.012, 4))
    x = tr.buf * (1 + 0.15 * np.sin(2 * np.pi * np.arange(tr.n) / tr.n * 8))
    return reverb(fft_filter(x, lo=30, hi=3800), 4.0, 0.45)

def tension():
    bpm = 66; beat = 60 / bpm; bars = 16; tr = Track(bars * 4 * beat)
    for b in range(bars):
        t0 = b * 4 * beat
        for k in range(4):  # heartbeat on each beat pair
            if k % 2 == 0:
                tr.add(t0 + k * beat, thump(48, 0.35, 0.9)); tr.add(t0 + k * beat + 0.22, thump(44, 0.35, 0.6))
        for k in range(8):  # clock tick
            tr.add(t0 + k * beat / 2, fft_filter(np.pad(noise_hit(0.03, 0.25, 160), (0, 2000)), lo=3000)[:1300] * (1.0 if k % 2 == 0 else 0.5))
        root = [38, 38, 39, 37][(b // 4) % 4]
        ost = [root, root + 12, root + 7, root + 12, root, root + 13, root + 7, root + 12]
        for k, n in enumerate(ost):
            tr.add(t0 + k * beat / 2, pad([midi(n)], beat / 2 * 0.95, 0.22, 0.6) * 1.0)
        if b % 4 == 2:
            tr.add(t0, pad([midi(n) for n in (62, 63, 68, 69)], 8 * beat, 0.35, 0.5))
    return reverb(fft_filter(tr.buf, lo=35, hi=5000), 2.0, 0.3)

out = sys.argv[1]
for name, fn in (('bgm_daily', daily), ('bgm_dark', dark), ('bgm_tension', tension)):
    x = fn(); write(f'{out}/{name}.wav', x); print(name, round(len(x) / SR, 1), 's')
