from transformers import pipeline
import pickle

# Initialize image classification pipeline
pipe = pipeline("image-classification", model="tonyassi/celebrity-classifier")


with open('hollywood_pipeline.pkl', 'wb') as archivo:
    pickle.dump(pipe, archivo)


