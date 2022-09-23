var padding = 15;
var len = 60;
var size = 10;
//draw canvas
var myCanvas = document.getElementById("myCanvas");
var enemyCanvas = document.getElementById("enemyCanvas");
const sendBtn = document.querySelector('#send');
var ctx=myCanvas.getContext("2d");
var ctx2=enemyCanvas.getContext("2d");
ctx.fillStyle = "navy";
ctx2.fillStyle = "navy";
ctx.font = "12px Calibri";
ctx2.font = "12px Calibri";
ctx.textAlign = "center";
ctx2.textAlign = "center";

function draw_canvas(context){
  for(var i=0;i<size;++i){
    context.fillText(i.toString(), (i+1)*padding + i*len + len/2, padding/2);
    context.fillText(i.toString(), padding/2, (i+1)*padding + i*len + len/2);
    for(var j=0;j<size;++j){
        if(sessionStorage.getItem("my"+i+j) == null)
          sessionStorage.setItem("my"+i+j, "navy");
        if(sessionStorage.getItem("enemy"+i+j) == null)
          sessionStorage.setItem("enemy"+i+j, "navy");

        if(context == ctx){
            // console.log("my" + sessionStorage.getItem("my"+i+j) + i + j)
            context.fillStyle = sessionStorage.getItem("my"+i+j)
        }
        else{
            // console.log("enemy" + sessionStorage.getItem("enemy"+i+j) + i + j)
            context.fillStyle = sessionStorage.getItem("enemy"+i+j)
        }
        
        var x = (i+1)*padding + i*len;
        var y = (j+1)*padding + j*len;
        context.fillRect(x, y, len, len);
    }
  }
}
draw_canvas(ctx);
draw_canvas(ctx2);

function draw_click(x, y){
    ctx2.fillStyle = "yellow";
    ctx2.fillRect((x+1)*padding + x*len,(y+1)*padding + y*len,len,len);
}

function draw_ship(xStart,yStart,xStop,yStop){
  ctx.fillStyle="green";
  if(xStart == xStop){
    for(var i=yStart;i<=yStop;++i){
      sessionStorage.setItem("my"+ xStart + i, "green");
    }
  }
  else{
    for(var i=xStart;i<=xStop;++i){
      sessionStorage.setItem("my"+ i + yStart, "green");
    }
  }
  draw_canvas(ctx);
}

function draw_move(context, move,x,y){
    switch(move){
        //miss
        case 1:
            if(context == ctx2){
                sessionStorage.setItem("enemy"+x+y,"grey");
                console.log("Your hit missed!");
                sendBtn.disabled = true;
            }
            else{
                sessionStorage.setItem("my"+x+y,"grey");
                console.log("Enemy hit missed!");
                sendBtn.disabled = false;
            }
        break;
        //hit
        case 2:
            if(context == ctx2){
                sessionStorage.setItem("enemy"+x+y,"red");
                console.log("You hit opponent!");
                sendBtn.disabled = false;
            }
            else{
                sessionStorage.setItem("my"+x+y,"red");
                console.log("Enemy hit you!");
                sendBtn.disabled = true;
            }
        break;
        //win
        case 3:
            console.log("Winning hit!");
            if(context == ctx2){
                sessionStorage.setItem("enemy"+x+y,"black");
                alert("You win")
            }
            else{
                sessionStorage.setItem("my"+x+y,"black");
                alert("You lost")
            }

            sendBtn.disabled = true;
        break;
    }
    if(context == ctx){
      draw_canvas(ctx)
    }
    else{
      draw_canvas(ctx2)
    }
    // var x = (x+1)*padding + x*len;
    // var y = (y+1)*padding + y*len;
    // context.fillRect(x,y,len,len)
    // context.fillStyle = "navy";
}
 
//click
var offsetLeft = enemyCanvas.offsetLeft + enemyCanvas.clientLeft;
var offsetTop = enemyCanvas.offsetTop + enemyCanvas.clientTop;
enemyCanvas.addEventListener("click", function(event) {
  draw_canvas(ctx2)
  var x = event.pageX - offsetLeft;
  var y = event.pageY - offsetTop;
  var readX;
  var readY;
  for(var i=0; i<size; ++i){
    if(x > (i+1)*padding + i*len && x < (i+1)*padding + (i+1)*len){
      readX = i;
    }
    if(y > (i+1)*padding + i*len && y < (i+1)*padding + (i+1)*len){
      readY = i;
    }
  }
  console.log(readX + " " + readY);
  document.getElementById("xCord").value = readX;
  document.getElementById("yCord").value = readY;
  sessionStorage.setItem("x", readX)
  sessionStorage.setItem("y", readY)
  draw_click(readX,readY);
});

