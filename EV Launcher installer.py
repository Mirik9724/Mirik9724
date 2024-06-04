from tkinter import *

import tempfile
import os
import sys
import zipfile
import platform
import requests
import threading
import subprocess

url = "https://github.com/Mirik9724/ev-launcher/archive/refs/heads/main.zip"

evlinstaller = Tk()
evlinstaller.title("EV Launcher installer")
evlinstaller.geometry("300x400")

def locate_evl():
    pass

lblacces = Label(text="Устоновщик EV Launcher")
lblacces.place(relx=0.5, rely=0.5, anchor='center')

lblinfo = Label(text=str(platform.node()) + " " + platform.system() + " " + platform.machine())
lblinfo.place(x = 5 ,y = 5)

def install():
    response = requests.get(url)

    file = tempfile.TemporaryFile()
    file.write(response.content)
    fzip = zipfile.ZipFile(file)
    fzip.extractall('')
    file.close()
    fzip.close()
    print("Успешно устоновленно")
    subprocess.call(['python', 'ev-launcher-main\evl_start.py'])
    sys.exit()

def install_thread():
    threadinstall = threading.Thread(target=install, args=())
    threadinstall.start()

btn = Button(text="Устоновить", command=install_thread, activebackground="#0a8b2e", background="green")
btn.place(x=5, y=310)
btn.configure(width=40, height=2)

btns = Button(text="ВЫЙТИ", command=evlinstaller.destroy, activebackground="#cd0000", background="red")
btns.place(x=5, y=355)
btns.configure(width=40, height=2)

evlinstaller.mainloop()