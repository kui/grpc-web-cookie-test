const { GreetRequest } = require('./greeting_pb');
const { GreetingServicePromiseClient } = require('./greeting_grpc_web_pb');
const greetingClient = new GreetingServicePromiseClient('http://localhost:8080');

window.GreetingClient = GreetingServicePromiseClient;
window.greetingClient = greetingClient;

document.querySelector('#greet').addEventListener('click', async () => {
  console.log('greet');
  const res = await greetingClient.greet(new GreetRequest());
  const tr = `<tr><td>${(new Date()).toString()}</td><td>${res.getMessage()}</td></tr>`;
  document.querySelector('#log').insertAdjacentHTML('afterbegin', tr);
});
