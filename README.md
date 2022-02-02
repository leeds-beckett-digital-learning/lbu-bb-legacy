# lbu-bb-legacy

This is a Java Web Application which conforms with the Blackboard Learn Building Block API. Building Blocks are a type of plugin for the Blackboard system.

This particular building block provides a user interface for system administrators to browse the so-called legacy file system of Blackboard Learn. This is, in effect, 
the file system of the host server. When a Blackboard Learn service runs on a cluster of server computers, the most important files are shared across all the servers.

Messing with files on the Blackboard Learn server could cause your system to fail so use this tool with great care. The authors accept no liability for any damage
done when you use this building block.

Then main purpose of the tool is to examine files that have been created by other building blocks. For example, it can be used to download a debugging log file created by
another building block.

The user interface is delibrately primative so that the source code and HTML can be kept very simple.
