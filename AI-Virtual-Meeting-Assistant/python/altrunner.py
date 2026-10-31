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
        print("Listening... Press F1 to send current text data to API.")
        recognizer.adjust_for_ambient_noise(source)  # Adjust for ambient noise
        while True:
            try:
                print("Listening...")
                audio = recognizer.listen(source, timeout=5, phrase_time_limit=10)  # Increase time limits
                text = recognizer.recognize_google(audio, language="en-IN")  # Indian English
                print("Text: " + text)
                text_buffer.append(text)  # Add text to buffer
                
                if keyboard.is_pressed('alt'):
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
