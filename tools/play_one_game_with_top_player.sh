#!/usr/bin/env sh
./playgame.py -I -O -E -e -o --player_seed 42 --end_wait=0.50 --verbose --log_dir game_logs --turns 500 --map_file maps/maze/maze_04p_01.map --turntime 500000 --loadtime 5000 "$@" "java -jar mark-alpha.jar" "java -jar bot/opponents/dummy.jar" "java -jar bot/opponents/XATHIS.jar" "java -jar bot/opponents/oldMyBot.jar"
