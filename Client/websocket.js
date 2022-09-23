 //websocket
 (function() {
    const sendBtn = document.querySelector('#send');
    const connect = document.querySelector('#connect');
    const xCord = document.querySelector('#xCord');
    const yCord = document.querySelector('#yCord');

    let ws;

    function init() {
      sendBtn.disabled = true;
      // if (ws) {
      //   ws.onerror = ws.onopen = ws.onclose = null;
      //   ws.close();
      // }
        ws = new WebSocket('ws://localhost:8080/battleships');
        ws.binaryType = 'arraybuffer'
        sendBtn.disabled = false;
        ws.onerror = () => {
          alert("Couldn't connect");
        }
        ws.onopen = () => {
          console.log('Connection opened!');
          var gid = Number (sessionStorage.getItem("gid"))
          if(gid == null){
            gid = 0;
          }
          ws.send(new Uint8Array([gid]))
        }
        ws.onmessage = ({ data }) => onReceive(data);
        ws.onclose = function() {
          console.log('Connection closing.');
          ws = null;
          sessionStorage.setItem("refreshed", "true")
        }
        sendBtn.onclick = function() {
          if (!ws) {
            console.log("No WebSocket connection.");
            return ;
          }
          // if(xCord.value == null || yCord.value == null){
            const binaryMessage = new Uint8Array([xCord.value, yCord.value]);
             ws.send(binaryMessage);
          // }
        }
    }

    function onReceive(message) {
      console.log("onReceive");
      console.log(message);
      xCord.value = '';
      yCord.value = '';
      interprete(message);
    }

    function interprete(message){
        var data = new Uint8Array(message)
        console.log("Proccessing message.");
        //opponent frame
        if(data[0] == 0){
            if(data[1] >= 1 && data[1] <= 3){
                draw_move(ctx,data[1],data[2],data[3]);
            }
            else{
                console.log("Invalid message");
                alert("Invalid message");
            }
        }
        //feedback frame
        if(data[0] == 1){
            if(data[1] >= 1 && data[0] <= 3){
                draw_move(ctx2,data[1],data[2],data[3]);
            }
            else{
                console.log("Invalid message");
                alert("Invalid message");
            }
        }
        //drawing ship
        if(data[0] == 2){
            console.log("Drawing ship.");
            draw_ship(data[1],data[2],data[3],data[4]);
            sendBtn.disabled = false;
        }
        //remembering game id
        if(data[0] == 3){
          sessionStorage.setItem("gid", data[1])
        }
    }

    init();
  })();