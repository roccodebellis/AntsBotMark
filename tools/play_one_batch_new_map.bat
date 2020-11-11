@Echo off
python ./playgame.py  "java -jar bot\MarksHive.jar" "java -jar bot\speedyconsole.jar" "java -jar bot\zacharydenton.jar" --end_wait=0.25 --map_file ".\maps\maze\maze_p03_01.map" --log_dir game_logs --turns 150 --player_seed 42 --verbose -e  --viewradius 55 --turntime 50000
@pause >nul