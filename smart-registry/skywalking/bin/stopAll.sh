kill -9 $(netstat -nlp | grep 11800 | awk '{print $7}' | awk -F"/" '{ print $1 }')
kill -9 $(netstat -nlp | grep 12800 | awk '{print $7}' | awk -F"/" '{ print $1 }')
kill -9 $(netstat -nlp | grep 30096 | awk '{print $7}' | awk -F"/" '{ print $1 }')