import socket
import json
from sql_init import session, heartRate, sessionmaker, engine

HOST, PORT = '', 8081

listen_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
listen_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
listen_socket.bind((HOST, PORT))
listen_socket.listen(1)
print('Serving HTTP on port {} ...'.format(PORT))
tmp = 0
while True:
    client_connection, client_address = listen_socket.accept()
    request = client_connection.recv(1024)
    print(request.decode())
    http_response = "{"
    tmp += 1
    Session = sessionmaker(bind=engine)
    session = Session()
 #   for data in session.query(heartRate).order_by(heartRate.time).filter(heartRate.time >= tmp).filter(heartRate.time <= tmp + 20):
    for data in session.query(heartRate).order_by(heartRate.time.desc()).limit(20):
        if tmp % 20 == 0:
            http_response += "}"
            break
        else:
            tmp += 1
            if tmp != 1: 
                http_response += ","
        http_response += "\"{}\":".format(data.time) + "[" + "\"{}\", \"{}\", \"{}\"".format(data.rdata, data.avgbpm, data.is_anxiety) + "]"
        # print(http_response)
    tmp = tmp % 100
    client_connection.send(http_response.encode())
    client_connection.close()
