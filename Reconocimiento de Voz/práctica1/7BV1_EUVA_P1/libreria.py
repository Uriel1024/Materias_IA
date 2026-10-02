import librosa
import librosa.display
import matplotlib.pyplot as plt
import time



for i in range(2):
	inicio = time.time()

	y, sr = librosa.load(f"audio{i+1}.mp3", sr=None)

	# Extract MFCCs (default 20 coefficients)
	mfccs = librosa.feature.mfcc(y=y, sr=sr, n_mfcc=20)

	#fin = time.time()
	fin = time.time()
	print(f"El calculo de los coeficientes usando librosa tardo {fin - inicio} segundos en ejecutarse .")

	# Visualize
	plt.figure(figsize=(10, 4))
	librosa.display.specshow(mfccs, x_axis='time', sr=sr)
	plt.colorbar(format='%+2.0f dB')
	plt.title(f'MFCC audio { i+1}')
	plt.tight_layout()
	plt.show()   