import speech_recognition as sr
import aiohttp
import asyncio
from pynput import keyboard
from collections import deque
import threading
import re

# Initialize recognizer
recognizer = sr.Recognizer()

# Buffer to store text data
text_buffer = deque(maxlen=10)  # Adjust maxlen based on your needs

# Define your API endpoint
api_endpoint = "http://localhost:8080/ai/prompt?chat="  # Replace with your actual endpoint

# Flag to manage listening state
listening = False

async def send_data_to_api(text_data):
    async with aiohttp.ClientSession() as session:
        async with session.get(api_endpoint + text_data) as response:
            async for line in response.content:
                print(line.decode('utf-8').strip())  # Print each line of the response

def preprocess_text(text):
    # Basic text cleaning: Remove unwanted characters and trim whitespace
    text = text.strip()
    text = re.sub(r'\s+', ' ', text)  # Replace multiple spaces with a single space
    return text

def capture_audio():
    global listening
    with sr.Microphone() as source:
        print("Press 'Alt+A' to start listening. Press 'Alt+S' to send current text data to API.")
        recognizer.adjust_for_ambient_noise(source)  # Adjust for ambient noise
        while True:
            if listening:
                try:
                    print("Listening...")
                    audio = recognizer.listen(source, timeout=5, phrase_time_limit=10)  # Increase time limits
                    text = recognizer.recognize_google(audio, language="en-IN")  # Indian English
                    processed_text = preprocess_text(text)
                    print("Text: " + processed_text)
                    text_buffer.append(processed_text)  # Add processed text to buffer

                except sr.UnknownValueError:
                    print("Could not understand audio")
                except sr.RequestError as e:
                    print(f"Could not request results; {e}")
                except Exception as e:
                    print(f"Error: {e}")

def on_activate_start_listening():
    global listening
    print("Started listening...")
    listening = True

def on_activate_send():
    global listening
    print("Sending data to API...")
    data_to_send = " ".join(text_buffer)
    asyncio.run(send_data_to_api(data_to_send))  # Send data to API and handle response
    text_buffer.clear()  # Clear buffer after sending
    listening = False  # Stop listening after sending

def on_press(key):
    global alt_pressed
    try:
        if key == keyboard.Key.alt_l:  # Handle Alt key press
            alt_pressed = True
        elif key == keyboard.KeyCode.from_char('a') and alt_pressed:  # Check if 'a' is pressed while Alt is held
            on_activate_start_listening()
            alt_pressed = False  # Reset Alt flag after starting listening
        elif key == keyboard.KeyCode.from_char('s') and alt_pressed:  # Check if 's' is pressed while Alt is held
            on_activate_send()
            alt_pressed = False  # Reset Alt flag after sending
    except AttributeError:
        print(f"Special key {key} pressed")

alt_pressed = False  # Flag to track Alt key state

if __name__ == "__main__":
    # Start the audio capture in a separate thread to allow key listening
    audio_thread = threading.Thread(target=capture_audio)
    audio_thread.start()

    # Start listening for key presses
    with keyboard.Listener(on_press=on_press) as listener:
        listener.join()
