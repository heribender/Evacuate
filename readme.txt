Evacuates deleted files and folders from your backup media to a graveyard.

This small application can be run before the ordinary backup run. It checks if there are folders and/or files in the backup folder which are no more present in the original folder, thus likely meaning the user of the original folder has deleted them. 

In order not to loose any artefact such abandoned folder/files will be copied to a third place, a kind of a recycler bin. 


Usage
-----

    evacuate.bat [options] OrigDir BackupDir EvacuateDir

where:
    OrigDir    : original directory
    BackupDir  : Backup directory
    EvacuateDir: Evacuation directory
Options:
    -d, --dry-run: no file operation is done, files to evacuate are listed on console
    -m, --move   : evacuated files are moved from backup to evacuate dir instead of copy

Needs Java8 (jre) to run	
	
TODO: build distribution zip with starter batch!
