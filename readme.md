# ASP.NET SignalR for Java and Android
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.signalr4j/signalr4j/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/com.github.signalr4j/signalr4j)

This is a fork of the original Microsoft SignalR library and also includes fixes from various developers since 2015.  I has been renamed to signalr4j to allow publishing to maven central under the com.github.signalr4j package.
**Note: This library is [NOT compatible](https://github.com/aspnet/SignalR/issues/883#issuecomment-336499189) with ASP.NET Core SignalR 2.0.**

ASP.NET SignalR is a new library for ASP.NET developers that makes it incredibly simple to add real-time web functionality to your applications. What is "real-time web" functionality? It's the ability to have your server-side code push content to the connected clients as it happens, in real-time.

## What can it be used for?
Pushing data from the server to the client (not just browser clients) has always been a tough problem. SignalR makes 
it dead easy and handles all the heavy lifting for you.

This library can be used from both regular Java or Android applications.

## Documentation
See the [documentation](http://asp.net/signalr)
	
## LICENSE
Apache 2.0 License

## Using the library in a Java application:

signalr4j is published on the Maven Central repository, so simply adding the maven coordinates to your dependencies section will get you started.  

```
<dependency>
    <groupId>com.github.signalr4j</groupId>
    <artifactId>signalr4j</artifactId>
    <version>2.0.1</version>
</dependency>
```




