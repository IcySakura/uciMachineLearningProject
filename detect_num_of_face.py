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


