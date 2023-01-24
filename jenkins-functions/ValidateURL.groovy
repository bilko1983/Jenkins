def call(url,time,unit) {
    timeout(time: time, unit: unit)  {
        def r = sh script: "wget -q $url -O /dev/null", returnStatus: true
        while (r != 0) {
            sh 'sleep 10'
            r = sh script: "wget -q $url -O /dev/null", returnStatus: true
        }
    }
}
