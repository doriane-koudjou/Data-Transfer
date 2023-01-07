# Data-Transfer
 Es handelt sich um eine einfache verteilte Anwendung zum Transfer von Dateien . Sie besteht aus einem Server-Prozess und mehreren Client-Prozessen . 
 
 
 # Tools 
 Für die Ausführung ist nur  eine IDE nötig  : Eclipse
 
 
 # Installation : 
 
 - Files  in die Eclipse-IDE importieren 
 - Zuerst Launchserver.java ausführen, um den Server zu starten. 
 - Danach  LaunchClient.java  ausführen, um den Client zu starten . 

# Verwendung
- LST fordert eine Liste der beim Datei-Server vorhandenen Dateien an
- PUT übertragt eine lokale, beim Client vorhandene Datei zum Server, der die Datei bei sich
ablegt .
- GET fordert eine Datei vom Server an, die nach Auslieferung beim Client abgelegt wird.
- DEL an löscht eine Datei auf dem Server.
- DAT enthält als Bytefolge den Inhalt der zu übertragenden Datei.
