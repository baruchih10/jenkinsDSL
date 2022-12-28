from flask import Flask, request, jsonify
import subprocess

app = Flask(__name__)

@app.route('/')
def hello():
	return "Hello World!"

@app.route('/containers')
def get_containers():
	output = subprocess.run(["docker", "ps"], stdout=subprocess.PIPE).stdout.decode('utf-8')
	return output.split('\n')[1:]

@app.route('/cache-me')
def cache():
	return "nginx will cache this response"

@app.route('/info')
def info():

	resp = {
		'connecting_ip': request.headers['X-Real-IP'],
		'proxy_ip': request.headers['X-Forwarded-For'],
		'host': request.headers['Host'],
		'user-agent': request.headers['User-Agent']
	}

	return jsonify(resp)

@app.route('/flask-health-check')
def flask_health_check():
	return "success"
