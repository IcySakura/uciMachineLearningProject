#Mood identificaiton
import cv2
import math
import sys

def sigmoid(x):
  return 1 / (1 + math.exp(-x))

emotions = ["neutral", "anger", "disgust",  "happy",  "surprise"]

faceDet = cv2.CascadeClassifier("./data/haarcascades/haarcascade_frontalface_default.xml")
faceDet_two = cv2.CascadeClassifier("./data/haarcascades/haarcascade_frontalface_alt2.xml")

faceDet_three = cv2.CascadeClassifier("./data/haarcascades/haarcascade_frontalface_alt.xml")
faceDet_four = cv2.CascadeClassifier("./data/haarcascades/haarcascade_frontalface_alt_tree.xml")
def process_face(img):
        frame = cv2.imread(img)
        #print(frame)
        
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        #4 different pre-trained classifier
        face = faceDet.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=10, minSize=(5, 5), flags=cv2.CASCADE_SCALE_IMAGE)
        face_two = faceDet_two.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=10, minSize=(5, 5), flags=cv2.CASCADE_SCALE_IMAGE)
        face_three = faceDet_three.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=10, minSize=(5, 5), flags=cv2.CASCADE_SCALE_IMAGE)
        face_four = faceDet_four.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=10, minSize=(5, 5), flags=cv2.CASCADE_SCALE_IMAGE)
        
        if(len(face) == 1):
            facefeatures = face
        elif len(face_two) == 1:
            facefeatures = face_two
        elif len(face_three) == 1:
            facefeatures = face_three
        elif len(face_four) == 1:
            facefeatures = face_four
        else:
            facefeatures = ''
        
        for(x, y, w, h) in facefeatures:

            gray = gray[y:y+h, x:x+w]
            
            try:
                out = cv2.resize(gray, (350, 350))
                
            except:
                pass
        return out

def predict_emotion(img):
    outcome = process_face(img)
    predictor = cv2.face.FisherFaceRecognizer_create()
    predictor.read('./data/mood_model.xml')
    pred,conf = predictor.predict(outcome)
    print(emotions[pred],'\n; Confidence is: ', conf/1000)

predict_emotion(sys.argv[1])