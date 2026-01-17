# ShadowStream

ShadowStream is designed to duplicate requests and forward them to multiple destinations for testing and monitoring purposes. It acts as a proxy server that intercepts incoming requests and sends copies to specified target servers while also forwarding the original request to its intended destination.

Example:

you want to test a new version of your API without affecting the production environment. ShadowStream can duplicate incoming requests to your production API and forward them to the new version of the API for testing.