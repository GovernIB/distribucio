-- Nova columna per guardar si el servei o procediment és comú.
ALTER TABLE dis_servei ADD comu NUMBER(1) DEFAULT 0 NOT NULL;
ALTER TABLE dis_procediment ADD comu NUMBER(1) DEFAULT 0 NOT NULL;