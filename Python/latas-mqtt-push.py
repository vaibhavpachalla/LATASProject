#!/usr/bin/env python
# encoding=utf8

import requests
import json
import base64
import sys
import time
import ssl
import random
from random import randint
import paho.mqtt.client as paho

# Set to True to point this script to localhost. Otherwise it will point to production.
LOCAL_DEV = False

PROTOCOL = 'https'

MY_CLIENT_ID = 'SETME'
MY_SECRET = 'SETME'

DRONE_MODEL = 'SETME'
DRONE_SERIAL_NUMBER = 'SETME'

CA_CERT_FILE = '../gd_bundle-g2-g1.crt'
VIEWPORT_CENTER_LATITUDE = 35.881137
VIEWPORT_CENTER_LONGITUDE = -78.791285
DEBUG = False
PUBLISH_SLEEP_TIME_SECONDS = 1.5
MQTT_KEEPALIVE_SECONDS = 60

MQTT_PORT = 8883

# At 38 degrees North latitude, one degree of latitude equals approximately 364,000 ft (69 miles)
DEGREE_PER_MILE = 1.0 / 69.0

STATUS_OK = 0
STATUS_FIRST_PACKET = 1
STATUS_LWT_PACKET = 7


def get_instance():
    url = 'http://geographies.flylatas.com/instance?lat={}&lon={}'.format(VIEWPORT_CENTER_LATITUDE, VIEWPORT_CENTER_LONGITUDE)

    # make the request
    results = requests.get(url)

    # get the results of the call
    json_result = json.loads(results.text)
    return json_result


def get_token(instance):
    url = '{}://{}/oauth/token?grant_type=client_credentials&scope=DEVICE'.format(PROTOCOL, instance)

    encoded_auth = base64.encodestring('{}:{}'.format(MY_CLIENT_ID, MY_SECRET)).rstrip().replace('\n', '')
    auth_header_value = 'Basic {}'.format(encoded_auth)

    # build the headers
    headers = {
        'Authorization': auth_header_value,
        'Accept': 'application/json',
        'Accept-Version': '1.0',
        'Content-Type': 'application/json'
    }

    # make the request
    results = requests.get(url, headers=headers)

    # get the results of the call
    json_result = json.loads(results.text)

    return json_result


def get_drone_id(instance, current_token):
    url = '{}://{}/api/drones/{}/{}'.format(PROTOCOL, instance, DRONE_MODEL, DRONE_SERIAL_NUMBER)

    # build the headers
    headers = {'Authorization': 'Bearer ' + current_token, 'Content-Type': 'application/json'}

    # make the request
    results = requests.get(url, headers=headers)

    # get the results of the call
    json_result = json.loads(results.text)
    if 'errors' in json_result:
        return None
    else:
        return json_result['droneId']


def get_publish_topic(instance, current_token, drone_id):
    url = '{}://{}/api/drones/{}/mqtt-topic'.format(PROTOCOL, instance, drone_id)

    # build the headers
    headers = {'Authorization': 'Bearer ' + current_token, 'Content-Type': 'application/json'}

    # make the request
    results = requests.get(url, headers=headers)

    # get the results of the load
    json_result = json.loads(results.text)

    return json_result['telemetryPublishingTopic']


def on_connect(client, userdata, flags, rc):
    print('Connected with result code %d' % (rc))

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe("$SYS/#")  # The callback for when a PUBLISH message is received from the server.


def on_publish(client, userdata, mid):
    print("Published message ID {}".format(str(mid)))


def publish_data(host, password, data_topic, drone_id):

    sequence_num = 0

    # Initialize MQTT client
    client = paho.Client()
    client.on_connect = on_connect
    client.on_publish = on_publish
    client.username_pw_set('oauth@{}'.format(drone_id), password)
    client.tls_insecure_set(value=True)
    client.tls_set(ca_certs=CA_CERT_FILE, certfile=None, keyfile=None, tls_version=ssl.PROTOCOL_TLSv1_2)
    port_to_use = MQTT_PORT

    # Create last will and testament (LWT) packet
    lwt_packet = {"id": drone_id, "sequence": sequence_num, "alt": 0, "lat": VIEWPORT_CENTER_LATITUDE,
                 "lon": VIEWPORT_CENTER_LONGITUDE, "track": 0, "speed": 0, "battery": -1, "lastPacket": True,
                 "status": STATUS_LWT_PACKET}
    lwt_topic = '/lastwill/{}'.format(drone_id)
    client.will_set(lwt_topic, payload=str(lwt_packet), qos=0, retain=False)

    print 'Attempting to connect...'
    client.connect(host, port=port_to_use, keepalive=MQTT_KEEPALIVE_SECONDS)
    print 'Connected. Starting loop...'
    client.loop_start()

    # Currently viewport on API end is limited to 4 square miles
    location_pivot = 2 * DEGREE_PER_MILE

    while True:
        sequence_num += 1

        # TODO Make this data variance more interesting than purely random
        latitude = VIEWPORT_CENTER_LATITUDE + random.uniform(-location_pivot, location_pivot)
        longitude = VIEWPORT_CENTER_LONGITUDE + random.uniform(-location_pivot, location_pivot)
        altitude = randint(1, 400)
        track = randint(0, 360)
        speed = randint(20, 45)
        battery_level = randint(1, 100)
        status = STATUS_FIRST_PACKET if sequence_num == 1 else STATUS_OK

        msg = {"id": drone_id, "sequence": sequence_num, "alt": altitude, "lat": latitude, "lon": longitude,
               "track": track, "speed": speed, "battery": battery_level, "status": status}
        print 'About to send message...'
        client.publish(data_topic, str(msg), qos=0)

        time.sleep(PUBLISH_SLEEP_TIME_SECONDS)


def main():
    if LOCAL_DEV:
        api_hostname = 'localhost:8080'
        data_hostname = 'localhost'
    else:
        # Get relevant LATAS instance based on geographic location
        host_info = get_instance()
        api_hostname = host_info['apiHostname']
        data_hostname = host_info['dataHostname']

    # Get API token
    token_response = get_token(api_hostname)
    token = token_response['access_token']
    if DEBUG:
        print token

    # Check if drone is registered with system
    drone_id = get_drone_id(api_hostname, token)
    if drone_id is None:
        print '"{}" platform with ID {} is not registered. Please register via dashboard or POST to {}://{}/api/drones'.format(
            DRONE_MODEL, DRONE_SERIAL_NUMBER, PROTOCOL, api_hostname)
        sys.exit(1)

    # Obtain topic to push data
    topic = get_publish_topic(api_hostname, token, drone_id)
    if DEBUG:
        print topic

    # Start loop to publish test data
    publish_data(data_hostname, token, topic, drone_id)


if __name__ == "__main__":
    main()
