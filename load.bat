@echo off
echo Workspace folder: %1
echo File to open: %2
eclipsec -nosplash -application org.eclipse.cdt.managedbuilder.core.headlessbuild -data %1 -importAll %1 -import AwesomeLibGDX -build all -cleanBuild all 
echo Opening in workspace %1 at file %2
eclipse --launcher.openFile %2 -data %1