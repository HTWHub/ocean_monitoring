import http.server
from prometheus_client import start_http_server, Counter

REQUEST_COUNT = Counter('app_requests_count', 'total all http requests count')

APP_PORT = 8000
METRICS_PORT = 8001

class HandleRequests(http.server.BaseHTTPRequestHandler):

  def do_GET(self):
    REQUEST_COUNT.inc()
    self.send_response(200)
    self.send_header("Content-type", "text/html")
    self.end_headers()

if __name__ == "__main__":
  start_http_server(METRICS_PORT)
  server = http.server.HTTPServer(("localhost", APP_PORT), HandleRequests)
  server.serve_forever()
