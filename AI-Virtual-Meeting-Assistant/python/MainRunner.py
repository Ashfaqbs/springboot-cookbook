import speech_recognition as sr
import os
import pyautogui
import time
import keyboard  # To detect key presses

# Initialize recognizer
recognizer = sr.Recognizer()

# Path to save the converted text in the same directory as the script
text_file_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "output.txt")

def capture_audio():
    with sr.Microphone() as source:
        print("Listening... Press Ctrl + D to stop.")
        while True:
            try:
                if keyboard.is_pressed('ctrl+d'):
                    print("Stopping...")
                    break
                audio = recognizer.listen(source, timeout=2, phrase_time_limit=5)
                text = recognizer.recognize_google(audio)
                print("Text: " + text)
                with open(text_file_path, "a") as f:
                    f.write(text + "\n")
            except sr.UnknownValueError:
                print("Could not understand audio")
            except sr.RequestError as e:
                print(f"Could not request results; {e}")
            except Exception as e:
                print(f"Error: {e}")

def feed_text_to_chat():
    with open(text_file_path, "r") as f:
        lines = f.readlines()
        if lines:
            for line in lines:
                pyautogui.typewrite(line)
                pyautogui.press('enter')
            with open(text_file_path, "w") as f:  # Clear the file after sending
                f.truncate(0)

if __name__ == "__main__":
    capture_audio()
    feed_text_to_chat()


# indian 

# import speech_recognition as sr
# import os
# import pyautogui
# import time
# import keyboard  # To detect key presses

# # Initialize recognizer
# recognizer = sr.Recognizer()

# # Path to save the converted text in the same directory as the script
# text_file_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "output.txt")

# def capture_audio():
#     with sr.Microphone() as source:
#         print("Listening... Press Ctrl + D to stop.")
#         while True:
#             try:
#                 if keyboard.is_pressed('ctrl+d'):
#                     print("Stopping...")
#                     break
#                 audio = recognizer.listen(source, timeout=2, phrase_time_limit=5)
#                 text = recognizer.recognize_google(audio, language="en-IN")  # Indian English
#                 print("Text: " + text)
#                 with open(text_file_path, "a") as f:
#                     f.write(text + "\n")
#             except sr.UnknownValueError:
#                 print("Could not understand audio")
#             except sr.RequestError as e:
#                 print(f"Could not request results; {e}")
#             except Exception as e:
#                 print(f"Error: {e}")

# def feed_text_to_chat():
#     with open(text_file_path, "r") as f:
#         lines = f.readlines()
#         if lines:
#             for line in lines:
#                 pyautogui.typewrite(line)
#                 pyautogui.press('enter')
#             with open(text_file_path, "w") as f:  # Clear the file after sending
#                 f.truncate(0)

# if __name__ == "__main__":
#     capture_audio()
#     feed_text_to_chat()
