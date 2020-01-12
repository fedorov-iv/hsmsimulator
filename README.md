Server that simulates **Thales HSM** responses

* Responses in format:

~~~
  byte[] responseBuffer = new byte[10];
  responseBuffer[0] = requestBuffer[0]; // starting byte (always 0)
  responseBuffer[1] = (byte)(responseBuffer.length - 2); //response message length (number of bytes following this one)
  responseBuffer[2] = requestBuffer[2]; // header byte (copied from request)
  responseBuffer[3] = requestBuffer[3]; // header byte (copied from request)
  responseBuffer[4] = requestBuffer[4]; // header byte (copied from request)
  responseBuffer[5] = requestBuffer[5]; // header byte (copied from request)
  responseBuffer[6] = requestBuffer[6]; // response code first byte (copied from request)
  responseBuffer[7] = (byte)(requestBuffer[7] + 1); // response code second byte + 1 (A0->A1, NC->ND and so on)
  responseBuffer[8] = 48; // 0 error code first byte
  responseBuffer[9] = 48; // 0 error code second byte
~~~
* Properties: app.properties
* Default port: 9999