# Database dump

`collabdesk_dump.sql` is a full MySQL 8 dump of the `collabdesk` schema (structure + demo data),
exported with MySQL Workbench (Server → Data Export → Self-Contained File, "Include Create Schema").

## Restore

```bash
mysql -u root -p < collabdesk_dump.sql
```

or in MySQL Workbench: **Server → Data Import → Import from Self-Contained File** → select this file → Start Import.

Then run the app with the `mysql` profile (see the main README). Demo accounts and passwords are listed there;
the dump only contains BCrypt password hashes.
