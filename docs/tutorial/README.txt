Como compilar arquivos .tex:
 $ pdflatex <arquivo>.tex
 $ pdflatex <arquivo>.tex

Na primeira execu��o, o pdf gerado cont�m "??" no lugar dos n�meros de
refer�ncia � se��es/c�digos/figuras. Por isso a repeti��o do comando. Na
primeira, foi gerado um �ndice e na segunda, o arquivo intermedi�rio de �ndice
j� existe e j� ser� usado. 

Se houverem refer�ncias bibliogr�ficas, acontece algo similar, por isso fa�a:
 $ pdflatex <arquivo>.tex
 $ bibtex <arquivo>.tex
 $ pdflatex <arquivo>.tex
 $ pdflatex <arquivo>.tex
