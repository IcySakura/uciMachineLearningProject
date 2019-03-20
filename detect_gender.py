# author: Arun Ponnusamy
# website: https://www.arunponnusamy.com

# import necessary packages
from keras.preprocessing.image import img_to_array
from keras.models import load_model
from keras.utils import get_file
import numpy as np
import argparse
import cv2
import os
import cvlib as cv
import sys

# handle command line arguments
##ap = argparse.ArgumentParser()
##ap.add_argument("-i", "--image", required=True,
##	help="path to input image")
##args = ap.parse_args()

image = cv2.imread(sys.argv[1])

# read input image
##image = cv2.imread(args.image)

if image is None:
    print("Could not read input image")
    exit()

model = load_model("./train_data/gender_detection.model")


face, confidence = cv.detect_face(image)

classes = ['man','woman']

for idx, f in enumerate(face):

    (startX, startY) = f[0], f[1]
    (endX, endY) = f[2], f[3]

    cv2.rectangle(image, (startX,startY), (endX,endY), (0,255,0), 2)

    face_crop = np.copy(image[startY:endY,startX:endX])

    face_crop = cv2.resize(face_crop, (96,96))
    face_crop = face_crop.astype("float") / 255.0
    face_crop = img_to_array(face_crop)
    face_crop = np.expand_dims(face_crop, axis=0)

    conf = model.predict(face_crop)[0]
    print(conf)
    print(classes)

    idx = np.argmax(conf)
    label = classes[idx]

    label = "{}: {:.2f}%".format(label, conf[idx] * 100)

    Y = startY - 10 if startY - 10 > 10 else startY + 10

    cv2.putText(image, label, (startX, Y),  cv2.FONT_HERSHEY_SIMPLEX,
                0.7, (0, 255, 0), 2)

cv2.imshow("gender detection", image)
         
cv2.waitKey()

##cv2.imwrite("gender_detection.jpg", image)

cv2.destroyAllWindows()
