import numpy as np
from scipy.fftpack import dct
import librosa
import librosa.display
import matplotlib.pyplot as plt
import time 

def compute_mfcc(audio, sample_rate, num_ceps=13, num_filters=40, fft_length=512, cep_lifter=22):
    # 1. Pre-emphasis
    audio = np.append(audio[0], audio[1:] - 0.97 * audio[:-1])

    # 2. Framing y Windowing
    win_length = int(round(0.025 * sample_rate))
    hop_length = int(round(0.010 * sample_rate))
    
    n_frames = 1 + int(np.floor((len(audio) - win_length) / hop_length))
    
    # Manejo seguro de strides dependiente del tipo de dato real del array
    itemsize = audio.strides[0]
    frames = np.lib.stride_tricks.as_strided(
        audio,
        shape=(n_frames, win_length),
        strides=(hop_length * itemsize, itemsize)
    ).copy()
    
    # Ventana de Hamming periódica
    window = np.hamming(win_length)
    frames *= window

    # 3. FFT y Espectro de Potencia normalizado
    mag_frames = np.abs(np.fft.rfft(frames, n=fft_length))
    power_spectrum = (1.0 / fft_length) * (mag_frames ** 2)

    # 4. Banco de Filtros Mel triangular estándar
    fmin = 0.0
    fmax = sample_rate / 2.0
    
    hz_to_mel = lambda f: 2595.0 * np.log10(1.0 + f / 700.0)
    mel_to_hz = lambda m: 700.0 * (10.0 ** (m / 2595.0) - 1.0)
    
    mel_points = np.linspace(hz_to_mel(fmin), hz_to_mel(fmax), num_filters + 2)
    hz_points = mel_to_hz(mel_points)
    
    # Mapeo a bins de FFT
    bins = np.floor((fft_length + 1) * hz_points / sample_rate).astype(int)
    fb = np.zeros((num_filters, int(fft_length // 2 + 1)))

    # Construcción de triángulos completos (subida y bajada)
    for m in range(1, num_filters + 1):
        f_left = bins[m - 1]
        f_center = bins[m]
        f_right = bins[m + 1]

        # Rampa ascendente
        if f_center > f_left:
            fb[m - 1, f_left:f_center] = (
                np.arange(f_left, f_center) - f_left
            ) / (f_center - f_left)
        
        # Rampa descendente
        if f_right > f_center:
            fb[m - 1, f_center:f_right] = (
                f_right - np.arange(f_center, f_right)
            ) / (f_right - f_center)

    # 5. Filtrado Mel y Logaritmo
    filter_banks = np.dot(power_spectrum, fb.T)
    filter_banks = np.where(filter_banks == 0, np.finfo(float).eps, filter_banks)
    log_filter_banks = np.log(filter_banks)

    # 6. DCT (Tipo 2, Orthonormal)
    mfccs = dct(log_filter_banks, type=2, axis=1, norm='ortho')[:, :num_ceps]

    # 7. Sinusoidal Liftering (mejora la robustez espectral)
    if cep_lifter > 0:
        n = np.arange(num_ceps)
        lift = 1.0 + (cep_lifter / 2.0) * np.sin(np.pi * n / cep_lifter)
        mfccs *= lift

    return mfccs


for i in range(2):
    inicio = time.time()
    # Cargar audio (librosa ya devuelve flotantes en [-1.0, 1.0])
    song, sample_rate = librosa.load(f"audio{i+1}.mp3", sr=None)

    # Cálculo de MFCCs
    mfccs = compute_mfcc(audio=song, sample_rate=sample_rate)
    fin = time.time()
    print(f"El calculo de los coeficientes de manera manual tardo {fin - inicio} en ejecutarse .")

    # Visualización
    plt.figure(figsize=(10, 4))
    # librosa.display espera formato (n_mfcc, n_frames)
    librosa.display.specshow(
        mfccs.T,
        sr=sample_rate,
        hop_length=int(round(0.010 * sample_rate)),
        x_axis='time'
    )
    plt.ylabel('Coeficiente MFCC')
    plt.colorbar()
    plt.title(f'MFCCs Audio {i+1}')
    plt.tight_layout()
    plt.show() 
