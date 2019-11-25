from flask import Flask, request, render_template, redirect, url_for
import os

app = Flask(__name__)

@app.route('/')
def hello():
    return redirect(url_for('login'))


@app.route('/login', methods=['GET','POST'])
def login():
    if request.method == 'POST': 
        account = request.values['user_name']
        password = request.values['user_password']
        cmdExec = "curl -X POST http://localhost:8181/RadiusAuthentication/UserCredential -u onos:rocks -d " + "'user=" +\
             account + "&" +"pass=" + password + "'"
        f = os.popen(cmdExec)
        result = f.read()
        if result=='Access-Accept':
            return redirect('https://www.nctu.edu.tw')
        return redirect(url_for('fail'))

    return render_template('index.html')

@app.route('/fail', methods=['GET','POST'])
def fail():
    if request.method == 'POST':
        return redirect(url_for('login'))
    return render_template('login_failed.html')


if __name__ == '__main__':
    app.debug = True
    app.run(host="192.168.44.198",port=5001)
