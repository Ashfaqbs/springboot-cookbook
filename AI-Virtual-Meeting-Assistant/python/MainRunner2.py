# import speech_recognition as sr
# import os
# import pyautogui
# import time
# import keyboard
# import requests
# from collections import deque

# # Initialize recognizer
# recognizer = sr.Recognizer()

# # Path to save the converted text in the same directory as the script
# text_file_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "output.txt")

# # Buffer to store text data
# text_buffer = deque(maxlen=10)  # Adjust maxlen based on duration (e.g., 10 for 15-30 seconds of data)

# def capture_audio():
#     with sr.Microphone() as source:
#         print("Listening... Press Alt to send last 15-30 seconds of text.")
#         while True:
#             try:
#                 audio = recognizer.listen(source, timeout=2, phrase_time_limit=5)
#                 text = recognizer.recognize_google(audio)  # Indian English
#                 print("Text: " + text)
#                 text_buffer.append(text)  # Add text to buffer
#                 with open(text_file_path, "a") as f:
#                     f.write(text + "\n")
                
#                 if keyboard.is_pressed('alt'):
#                     send_last_text_data()  # Send data to API when Alt is pressed
                
#             except sr.UnknownValueError:
#                 print("Could not understand audio")
#             except sr.RequestError as e:
#                 print(f"Could not request results; {e}")
#             except Exception as e:
#                 print(f"Error: {e}")

# def send_last_text_data():
#     # Prepare data from buffer
#     data_to_send = "\n".join(text_buffer)
    
#     # Define your API endpoint
#     api_endpoint = "http://localhost:5000/api/receive_data"  # Replace with your actual endpoint
    
#     # Send data to the API
#     try:
#         response = requests.post(api_endpoint, data={"text": data_to_send})
#         print("API Response:", response.text)  # Print the API response in terminal
#     except Exception as e:
#         print(f"Error sending data to API: {e}")

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


# import speech_recognition as sr
# import os
# import pyautogui
# import time
# import keyboard
# import requests
# from collections import deque
# import threading

# # Initialize recognizer
# recognizer = sr.Recognizer()

# # Path to save the converted text in the same directory as the script
# text_file_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "output.txt")

# # Buffer to store text data
# text_buffer = deque(maxlen=10)  # Adjust maxlen based on duration (e.g., 10 for 15-30 seconds of data)

# def capture_audio():
#     with sr.Microphone() as source:
#         print("Listening... Press Alt to send last 15-30 seconds of text.")
#         while True:
#             try:
#                 audio = recognizer.listen(source, timeout=2, phrase_time_limit=5)
#                 text = recognizer.recognize_google(audio)  # Indian English
#                 print("Text: " + text)
#                 text_buffer.append(text)  # Add text to buffer
#                 with open(text_file_path, "a") as f:
#                     f.write(text + "\n")
                
#             except sr.UnknownValueError:
#                 print("Could not understand audio")
#             except sr.RequestError as e:
#                 print(f"Could not request results; {e}")
#             except Exception as e:
#                 print(f"Error: {e}")

# def send_last_text_data():
#     # Prepare data from buffer
#     data_to_send = "\n".join(text_buffer)
    
#     # Define your API endpoint
#     api_endpoint = "http://localhost:8080/ai/prompt?chat="  # Replace with your actual endpoint
    
#     # Send data to the API
#     try:
#         response = requests.post(api_endpoint, data={"text": data_to_send})
#         print("API Response:", response.text)  # Print the API response in terminal
#     except Exception as e:
#         print(f"Error sending data to API: {e}")

# def monitor_keypress():
#     while True:
#         if keyboard.is_pressed('alt'):
#             send_last_text_data()  # Send data to API when Alt is pressed
#             time.sleep(1)  # Prevent multiple triggers by adding a delay

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
#     # Start the key press monitoring thread
#     keypress_thread = threading.Thread(target=monitor_keypress, daemon=True)
#     keypress_thread.start()
    
#     # Start capturing audio
#     capture_audio()
    
#     # Optionally feed text to chat
#     # feed_text_to_chat()

# import speech_recognition as sr
# import time
# import keyboard
# import aiohttp
# import asyncio
# from collections import deque

# # Initialize recognizer
# recognizer = sr.Recognizer()

# # Buffer to store text data
# text_buffer = deque(maxlen=10)  # Adjust maxlen based on your needs

# # Define your API endpoint
# api_endpoint = "http://localhost:8080/ai/prompt?chat="  # Replace with your actual endpoint

# async def send_data_to_api(text_data):
#     async with aiohttp.ClientSession() as session:
#         async with session.get(api_endpoint + text_data) as response:
#             async for line in response.content:
#                 print(line.decode('utf-8').strip())  # Print each line of the response

# def capture_audio():
#     with sr.Microphone() as source:
#         print("Listening... Press Ctrl+S to send current text data to API.")
#         while True:
#             try:
#                 audio = recognizer.listen(source, timeout=2, phrase_time_limit=5)
#                 text = recognizer.recognize_google(audio, language="en-IN")  # Indian English
#                 print("Text: " + text)
#                 text_buffer.append(text)  # Add text to buffer
                
#                 # Join buffer texts and encode for URL
#                 data_to_send = " ".join(text_buffer)
                
#                 if keyboard.is_pressed('ctrl+s'):
#                     print("Sending data to API...")
#                     asyncio.run(send_data_to_api(data_to_send))  # Send data to API and handle response
#                     text_buffer.clear()  # Clear buffer after sending
#                     time.sleep(1)  # Prevent multiple triggers
                
#             except sr.UnknownValueError:
#                 print("Could not understand audio")
#             except sr.RequestError as e:
#                 print(f"Could not request results; {e}")
#             except Exception as e:
#                 print(f"Error: {e}")

# if __name__ == "__main__":
#     capture_audio()


import speech_recognition as sr
import time
import keyboard
import aiohttp
import asyncio
from collections import deque

# Initialize recognizer
recognizer = sr.Recognizer()

# Buffer to store text data
text_buffer = deque(maxlen=10)  # Adjust maxlen based on your needs

# Define your API endpoint
api_endpoint = "http://localhost:8080/ai/prompt?chat="  # Replace with your actual endpoint

async def send_data_to_api(text_data):
    async with aiohttp.ClientSession() as session:
        async with session.get(api_endpoint + text_data) as response:
            async for line in response.content:
                print(line.decode('utf-8').strip())  # Print each line of the response

def capture_audio():
    with sr.Microphone() as source:
        print("Listening... Press Ctrl+S to send current text data to API.")
        recognizer.adjust_for_ambient_noise(source)  # Adjust for ambient noise
        while True:
            try:
                print("Listening...")
                audio = recognizer.listen(source, timeout=5, phrase_time_limit=10)  # Increase time limits
                text = recognizer.recognize_google(audio, language="en-IN")  # Indian English
                print("Text: " + text)
                text_buffer.append(text)  # Add text to buffer
                
                if keyboard.is_pressed('n'):
                    print("Sending data to API...")
                    data_to_send = " ".join(text_buffer)
                    asyncio.run(send_data_to_api(data_to_send))  # Send data to API and handle response
                    text_buffer.clear()  # Clear buffer after sending
                    time.sleep(1)  # Prevent multiple triggers
                
            except sr.UnknownValueError:
                print("Could not understand audio")
            except sr.RequestError as e:
                print(f"Could not request results; {e}")
            except Exception as e:
                print(f"Error: {e}")

if __name__ == "__main__":
    capture_audio()
