#!/usr/bin/env bash
clj -Spom
clj -A:depstar
printf "Clojars Username: "
read -r username
stty -echo
printf "Clojars Password: "
read -r password
printf "\n"
stty echo
CLOJARS_USERNAME=${username} CLOJARS_PASSWORD=${password} clj -A:deploy
rm blanket.jar
