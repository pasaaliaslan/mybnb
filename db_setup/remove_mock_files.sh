#! /bin/bash

data_directory="/usr/local/mysql/data/"

if [ $# -ge 1 ]
then
  data_directory=$1
fi

sudo rm -r $data_directory/mock_data
