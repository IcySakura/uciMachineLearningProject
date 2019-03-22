import os
import numpy as np
import cv2
import sys

subjects = ["", "Joe Belfiore", "Robert Downey Jr."]

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
			face, rect = detect_face(image)
			if face is not None:
				faces.append(face)
				labels.append(label)

	#cv2.destroyAllWindows()
	#cv2.waitKey(1)
	#cv2.destroyAllWindows()
	return faces, labels

def draw_rectangle(img, rect):
	(x, y, w, h) = rect
	cv2.rectangle(img, (x, y), (x+w, y+h), (0, 255, 0), 2)

def draw_text(img, text, x, y):
	cv2.putText(img, text, (x, y), cv2.FONT_HERSHEY_PLAIN, 1.5, (0, 255, 0), 2)

def predict(test_img):
	img = test_img.copy()
	print(img)
	face, rect = detect_face(img)
	print(face)
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
print("Prediction complete")
predicted_img = predict(userInput)
#cv2.imshow(subjects[1], cv2.resize(predicted_img1, (400, 500)))
#cv2.imshow("Output", cv2.resize(predicted_img, (400, 500)))
#cv2.waitKey(0)
#cv2.destroyAllWindows()

