"""
The FaceRecognizer.py module (as a part of the FaceRecognizer API) is modified based on code 
template provided on the Github Repository:
https://github.com/informramiz/opencv-face-recognition-python (Copyright (c) 2017 Ramiz Raja), 
which is also associated with the following OpenCV guide written by Adrian Rosebrock on the 
following page:
https://www.pyimagesearch.com/2018/07/19/opencv-tutorial-a-guide-to-learn-opencv/ . The module 
utilizes OpenCV and Local Binary Patterns Histograms (LBPH) Face Recognizer to train the model
with input from the user at real-time. More training data will make the prediction more accurate.
The default collection we provided here is 24 photos of 2 different people (12 of each person
in folders s1 and s2 under the following directory: ./input/faceRecognizerData/trainData 
, respectively).
"""

import os
import numpy as np
import cv2
import sys

subjects = ["", "s1", "s2"]
"""
These temporary names are for the sample photos in our default collection. You will have
to modify the variables to correspond with your actual sample data.
"""

def detect_face(img):
	gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
	face_cascade = cv2.CascadeClassifier('./data/lbpcascades/lbpcascade_frontalface.xml')
	faces = face_cascade.detectMultiScale(gray, scaleFactor=1.2, minNeighbors=5)
	if (len(faces) == 0):
		return None, None
	else:
		x, y, w, h = faces[0]
		return gray[y:y+w, x:x+h], faces[0]

def prepare_training_data(data_folder_path):
	dirs = os.listdir(data_folder_path)
	faces = []
	labels = []
	for dir_name in dirs:
		if not dir_name.startswith("s"):
			continue
		label = int(dir_name.replace("s", ""))



		subject_dir_path = data_folder_path + "/" + dir_name




		subject_images_names = os.listdir(subject_dir_path)
		for image_name in subject_images_names:
			if image_name.startswith("."):
				continue
			image_path = subject_dir_path + "/" + image_name
			image = cv2.imread(image_path)
			#cv2.imshow("Training on image...", image)
			# cv2.imshow("Training on image...", cv2.resize(image, (400, 500)))
			#cv2.waitKey(100)
			"""
			You can uncomment the second line and the third line above to display
			the current image that is being learned by the model.
			"""
			face, rect = detect_face(image)
			if face is not None:
				faces.append(face)
				labels.append(label)

	#cv2.destroyAllWindows()
	#cv2.waitKey(1)
	#cv2.destroyAllWindows()
	"""
	You can comment out the second line and third line above so it will automatically
	close the image window after the model "learns" all the photos in the sample data.
	"""
	return faces, labels

def draw_rectangle(img, rect):
	(x, y, w, h) = rect
	cv2.rectangle(img, (x, y), (x+w, y+h), (0, 255, 0), 2)

def draw_text(img, text, x, y):
	cv2.putText(img, text, (x, y), cv2.FONT_HERSHEY_PLAIN, 1.5, (0, 255, 0), 2)

def predict(test_img):
	img = test_img.copy()
	face, rect = detect_face(img)
	label = face_recognizer.predict(face)
	print(label)
	label_text = subjects[label[0]]
	draw_rectangle(img, rect)
	draw_text(img, label_text, rect[0], rect[1]-5)
	return img


print("Preparing data...")
faces, labels = prepare_training_data("./input/faceRecognizerData/trainData/")
print("Data prepared")
print("Total faces: ", len(faces))
print("Total labels: ", len(labels))

face_recognizer = cv2.face.LBPHFaceRecognizer_create()
face_recognizer.train(faces, np.array(labels))

print("Predicting images...")
#test_img1 = cv2.imread("./test_img/test1.jpg")
#test_img2 = cv2.imread("./test_img/test2.jpg")
#predicted_img1 = predict(test_img1)
#predicted_img2 = predict(test_img2)
userInput = cv2.imread(sys.argv[1])

"""
When calling this py module directly from server command line, please remember to
add in another argument which is the location of test input for the model to
recognize/predict after learning your sample data (again, should be under this directory:
./input/faceRecognizerData/trainData).
"""

print("Prediction:", end='') 
predicted_img = predict(userInput)
#cv2.imshow(subjects[1], cv2.resize(predicted_img1, (400, 500)))
#cv2.imshow("Output", cv2.resize(predicted_img, (400, 500)))
#cv2.waitKey(0)
#cv2.destroyAllWindows()
"""
You can comment the four lines above to see the visual output of the
recognition result.
"""

