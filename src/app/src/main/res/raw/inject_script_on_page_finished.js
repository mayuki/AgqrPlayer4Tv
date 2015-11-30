(function(){

    var videoE = document.querySelector("video");
    videoE.play();

    // なんか再生が止まることがある、ので止まったら雑リスタート
    var timerId;

    function restart() {
        timerId = setTimeout(function () {
            window.location.reload();
        }, 3000);
    };

    videoE.addEventListener('ended', function () {
        console.log('ended');
        restart();
    });

    videoE.addEventListener('paused', function () {
        console.log('paused');
        restart();
    });

    videoE.addEventListener('error', function () {
        console.log('error');
        restart();
    });

    videoE.addEventListener('stalled', function () {
        console.log('stalled');
    });

    videoE.addEventListener('playing', function () {
        console.log('playing');
        if (timerId != null) {
            clearTimeout(timerId);
            timerId = null;
        }
    });
})();