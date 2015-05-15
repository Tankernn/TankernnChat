# Changelog

## Version 0.3
* Much improved GUI for client, displaying users online and colored messages
* Many commands added, including time-based ban, private message and more
* Support for multiple channels
* Server console is now considered a client, this means that every time a command is executed, a reference to the caller is supplied. This allows direct communication with the sender to return output.
* Messages are sent as Message-objects instead of strings, this allows for more versatile communication, including listing current users online.

## Version 0.2
* Support for clients executing commands
* Permission system to allow certain clients to use certain commands
* Auto-generating config file for server
