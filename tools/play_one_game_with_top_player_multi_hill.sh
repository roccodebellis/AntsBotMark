#!/usr/bin/env sh
./playgame.py -I -O -E -e -o --player_seed 42 --end_wait=0.25 --verbose --log_dir game_logs --viewradius 77 --turns 300 --map_file maps/multi_hill_maze/maze_04p_02.map --turntime 10000 --loadtime 5000 "$@" "java -jar mark-alpha.jar" "java -jar bot/opponents/Mark_withoutCombat.jar" "java -jar bot/opponents/XATHIS.jar" "java -jar bot/opponents/oldMyBot.jar"
