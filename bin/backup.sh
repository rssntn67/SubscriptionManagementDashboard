#! /bin/bash
/Library/PostgreSQL/10/bin/pg_dump -U postgres -Fc -f ~/runtime/backups/smd.`date '+%Y%m%d'`-pgsql.gz smd
