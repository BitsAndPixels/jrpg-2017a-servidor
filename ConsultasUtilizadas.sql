-- CONSULTAS USUARIO
select * from registro;

delete from registro where usuario='nuevoUser';

-- CONSULTAS PERSONAJE

select * from personaje;


-- CONSULTAS ITEM
select * from item;

alter table item 
add column
tipo integer;

alter table item 
add column
nombre varchar(15)
;

describe item;

insert into item(idItem,tipo,bonoAtaque,nombre)
values(1,1,2,"Espada");

insert into item(idItem,tipo,bonoDefensa,nombre)
values(2,2,2,"Escudo");

insert into item(idItem,tipo,bonoAtaque,bonoDefensa,BonoMagia,bonoSalud,bonoEnergia,nombre)
values(3,1,3,0,0,0,0,"Hacha");

insert into item(idItem,tipo,bonoAtaque,bonoDefensa,BonoMagia,bonoSalud,bonoEnergia,nombre)
values(4,5,0,5,0,5,0,"Armadura");

insert into item(idItem,tipo,bonoAtaque,bonoDefensa,BonoMagia,bonoSalud,bonoEnergia,nombre)
values(5,3,0,1,0,0,0,"Botas");

insert into item(idItem,tipo,bonoAtaque,bonoDefensa,BonoMagia,bonoSalud,bonoEnergia,nombre)
values(6,4,0,3,0,0,0,"Casco");

insert into item(idItem,tipo,bonoAtaque,bonoDefensa,BonoMagia,bonoSalud,bonoEnergia,nombre)
values(7,6,0,0,5,0,0,"Anillo Magico");

select count(1) from item;


-- CONSULTAS INVENTARIO

select * from inventario;

-- CONSULTAS MOCHILA

select * from mochila;

select * from personaje;

-- 10	15	10	175	160	cortex
-- 10	15	10	175	160	cortex

select * from mochila where idMochila in (35,38);
select * from inventario where idInventario in (35,38);

select * from mochila where idMochila in (39,40);
select * from inventario where idInventario in (39,40);

/*
PRUEBAS DE MODIFICACION DE STATS: 

10	10	15	65	50
15	10	10	105	95

10	10	15	115	90
15	10	10	105	95
*/