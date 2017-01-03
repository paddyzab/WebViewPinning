### WebViewPinning
##### Why?
Certificate Pinning is one of the techniques which allow protect your api
Against Man in the Middle attacks. Simply by making the handshake and connection
impossible when system detects that SSL connection is going through the proxy service.

Since WebView is in use inside of safe space of your system. Your responsibility is to protect
uses from the possible malicious behaviour which would replace your standard Q&A page
with
phishing attempt. After all user is in space after all required validation was performed.

##### How?
In order to pin host certificate. First thing you need to get is the certificate from your
DevOps team. You need to deliver it inside the application binary.
Later we will discuss how to do it in a secured way.
Inside of the WebView context you can ask widget to return you current host certificate.
The problem is that the format you will receive will miss Certificate fingerprint data which will allow you
effective comparison against the certificate provided by your DevOps team.
Method generateX509Certificate(SslCertificate certificate) by using saveState method will try to extract X509Certificate which contains fingerprint data.
Later we will use this fingerprint for comparison against fingerprint of a provided certificate.

#####How to test?
Install and configure one of the popular alternatives for proxing and monitoring HTTP traffic.
[Charles Proxy](https://www.charlesproxy.com "Charles Proxy")  or
[Burp from PortSwigger](https://portswigger.net/burp/ "Burp")
Configure your emulator or device to connect through the proxy, and verify the behaviour of test project.


