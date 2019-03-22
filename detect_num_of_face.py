'''
This is a machine learning Python 3 file for detecting number of faces in the image.
The original credit is given to:
    https://medium.com/analytics-vidhya/how-to-build-a-face-detection-model-in-python-8dc9cecadfe9
We have modified the file to make it works for our APIs.
It has utilized a pre-trained model call “haarcascade_frontalface_default.xml,” 
which is stored under the “data” folder in the root. The model is provided in OpenCV machine learning library.
# Please refer to our API documentation to get more detail.
'''

import cv2
import sys

# This python 3 file will take one parameter as input, which is the location of the img
# This python 3 file will print the num of face detected in the img

img = cv2.imread(sys.argv[1], cv2.IMREAD_COLOR)
imgtest1 = img.copy()
imgtest = cv2.cvtColor(imgtest1, cv2.COLOR_BGR2GRAY)
facecascade = cv2.CascadeClassifier('./data/haarcascades/haarcascade_frontalface_default.xml')

faces = facecascade.detectMultiScale(imgtest, scaleFactor=1.2, minNeighbors=5)

print(len(faces), end='')


