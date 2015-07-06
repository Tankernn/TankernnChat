[![Build Status](https://travis-ci.org/Tankernn/TankernnChat.svg?branch=master)](https://travis-ci.org/Tankernn/TankernnChat) [![Coverage](http://codecov.io/github/Tankernn/TankernnChat/coverage.svg?branch=master)](http://codecov.io/github/Tankernn/TankernnChat?branch=master)

# TankernnChat
A chat server and client pair

### About
The program is meant for chatting and briefly consists of a client part and a server part. X number of clients can connect to the server concurrently and send messages to other clients on the same channel. In addition to the basic functionality is some color coding of messages and reasonably functioning permission-system. There are a number of commands built-in, but more are needed.

### The goals of the project are:
* The system shall be as stable as possible.
* The system shall require as little computing power as possible.
* The system shall use as little bandwidth as possible.

### How to use
1. Download the latest version from the 'releases' tab
  1. Download server
  2. Tell all users to download the client
2. Run the server by running `java -jar /path/to/ChatServer.jar` in a command prompt
3. You will need to port forward if you're going to use the server over the internet
4. Clients connect by double-clicking the ChatClient.jar file and typing in IP/domain-name, port and username and clicking 'OK'
5. Use `/help` on client or server for a list of commands

### How to collaborate
1. Ask me for collaborator permissions
2. Clone the repo to your device
3. If you're using Eclipse:
  1. Set your GitHub directory as workspace
  2. Create a new project named "TankernnChat", **exactly** like that
  3. If you see a dialog asking you to use the project present with the same name, click OK
  4. You're good to go!
4. If you're using a normal editor, just edit the files as usual.
