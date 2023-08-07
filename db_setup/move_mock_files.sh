#! /bin/bash

data_directory="/usr/local/mysql/data/"

if [ $# -ge 1 ]
then
  data_directory=$1
fi

sudo cp -r mock_data $data_directory
