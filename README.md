# MyBnB

## Description

## Blueprints

Blueprints are documented separately for easier consumption.

### Database related Documents (under ./doc/db)

- __assumptions.md__: Assumptions for the database design.

- __er.pdf__: The ER Diagram of the system.

- __schemas.md__: All schemas used for mysql db.

### API related Documents (under ./doc/api)

- __client.md__: Corresponds to the functionalities demanded under `Operations to Support` and `Queries to Support` sections in the project document.

- __report.md__: Corresponds to the functionalities demanded under `Reports to Support` section in the project document.

- __toolkit.md__: Corresponds to the functionalities demanded under `Host Toolkit` section in the project document.

## Setup

### Database

1. Run `cd ./db_setup`.

2. Create a database in MySql.

    1. (Inside shell/terminal) Run `mysql -p -u [dbuser (usually 'root')]`, where `dbuser` is the username used for the database. You may be asked to enter password.
    2. (Inside MySql) Run `create database [dbname];`, where `dbname` can be any valid string as a database name.
    3. (Inside MySql) Run `SET GLOBAL local_infile=1;`
    4. (Inside MySql) Run `exit;`.

3. Create schemas.

    1. (Inside shell/terminal) Run `mysql -p -u [dbuser (usually 'root')] [dbname] < db_setup.sql`, where `dbuser` and `dbname` should match what was inputted during database creation.

4. Prepopulate data.

    1. (Inside shell/terminal) Run `sh move_mock_files.sh [mysql_data_directory (default: "/usr/local/mysql/data/")]`, which will put all mock data files in mysql territory, so there will be no permission issues.

    2. (Inside shell/terminal) Run `mysql --local-infile=1 -p -u [dbuser (usually 'root')] [dbname] < prepopulate_data.sql`, which will populate all newly created tables with the data in copied mock data files.

    3. (Inside shell/terminal) Run `sh remove_mock_files.sh [mysql_data_directory (default: "/usr/local/mysql/data/")]` to remove all mock data files (which are not needed after this point as data are in tables)

### Backend
1. Rename `.env-template` to `.env`.
2. Fill the information in `.env` according to your system.
3. [Run the server program]
