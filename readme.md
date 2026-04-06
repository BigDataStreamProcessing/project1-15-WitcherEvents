# Charakterystyka danych
Poszczególni wiedźmini z uniwersum *Wiedźmina* wykonują zlecenia na potwory 
(reprezentując szkołę swojego cechu wiedźmińskiego). 
W tym celu wykorzystują pomocne eliksiry, które wpływają na poziom 
zatrucia ich organizmu. Za wykonane zadanie wiedźmini zdobywają zapłatę.

W strumieniu pojawiają się zdarzenia zgodne ze schematem `WitcherEvent`.

```
create json schema WitcherEvent(witcher string, its string, 
  ets string, school string, monster string, payment int, difficulty_level string, 
location string, potion string, poisoning_lvl int);
```

Każde zdarzenie związane z jest z faktem odebrania nagrody 
(określona ilości złota) przez określonego wiedźmina z określonej szkoły. 

Każde zdarzenie zawiera dodatkowo informacje na temat zabitego potwora, 
poziom trudności zlecenia (Easy/Medium/Hard/Very Hard/Lethal), 
lokalizacji zlecenia, a także nazwy użytego eliksiru i poziomu jego toksyczności.

Dane uzupełnione są o etykiety czasowe:
- `ets` - która odzwierciedla moment odbioru zapłaty za zlecenie. 
   Może się on losowo spóźniać w stosunku do czasu systemowego 
   maksymalnie do 1 minuty i jest zaokrąglony do 1 sekundy.
- `its` - czas rejestracji zdarzenia odebrania zapłaty w systemie. 
   Jest on zaokrąglony do 1 sekundy.


# Opis atrybutów
Znaczenie poszczególnych atrybutów w każdym zdarzeniu:

- `witcher` - postać wykonująca zlecenie
- `its` - czas rejestracji zdarzenia w systemie
- `ets` - czas odebrania zapłaty za wykonane zlecenie
- `school` - szkoła cechu wiedźmińskiego (którą reprezentuje 
   wiedźmin wykonujący zlecenie)
- `monster` - potwór, na którego wiedźmin dostał zlecenie
- `payment` - ilość złota, którą otrzymał wiedźmin za wykonane zlecenie
- `difficulty_level` - poziom trudności wykonywanego zlecenia
- `location` - miejsce, w którym wiedźmin wykonuje zlecenie
- `potion` - eliksir, wykorzystany przez wiedźmina podczas walki
- `poisoning_level` - poziom toksyczności eliksiru

# Zadania
Opracuj rozwiązania poniższych zadań. 
* Opieraj się strumieniu zdarzeń zgodnych ze schematem `WitcherEvent`
* W każdym rozwiązaniu możesz skorzystać z jednego lub kilku poleceń EPL.
* Ostatnie polecenie będące ostatecznym rozwiązaniem zadania musi 
   * być poleceniem `select` 
   * posiadającym etykietę `answer`, przykładowo:
```aidl
@name('answer') SELECT witcher, school, monster, payment, 
                       difficulty_level, location,
                       potion, poisoning_lvl, its, ets
FROM WitcherEvent#ext_timed(java.sql.Timestamp.valueOf(its).getTime(), 3 sec);
```

## Zadanie 1
Obliczaj sumy zarobionego złota przez poszczególne szkoły wiedźmińskie 
w ciągu ostatniej minuty rejestracji zdarzeń.

Wyniki powinny zawierać, następujące kolumny:
- `school` - nazwa szkoły
- `sumpayment` - suma zarobionego złota przez szkołę wiedźmińską w 
  ciągu ostatniej minuty.

## Zadanie 2
Wykrywaj przypadki, w których otrzymana zapłata przekroczyła 245 sztuk złota.

Wyniki powinny zawierać wszystkie kolumny dotyczące zdarzenia:
- `witcher` - postać wykonująca zlecenie
- `its` - czas rejestracji zdarzenia w systemie
- `ets` - czas odebrania zapłaty za wykonane zlecenie
- `school` - szkoła cechu wiedźmińskiego (którą reprezentuje
  wiedźmin wykonujący zlecenie)
- `monster` - potwór, na którego wiedźmin dostał zlecenie
- `payment` - ilość złota, którą otrzymał wiedźmin za wykonane zlecenie
- `difficulty_level` - poziom trudności wykonywanego zlecenia
- `location` - miejsce, w którym wiedźmin wykonuje zlecenie
- `potion` - eliksir, wykorzystany przez wiedźmina podczas walki
- `poisoning_level` - poziom toksyczności eliksiru

## Zadanie 3
Wykrywaj odebrania nagrody za zlecenia, dla których suma poziomów 
toksyczności eliksirów dla zleceń zarejestrowanych przez tego wiedźmina 
w ciągu ostatniej pół minuty przekroczyła wartość 20.

Wyniki powinny zawierać, następujące kolumny:
- `witcher` - nazwa wiedźmina
- `total_poisoning` - suma punktów toksyczności eliksirów
- `its` - czas rejestracji odbioru nagrody

## Zadanie 4

Wiedźmini podróżują i wykonują różnorodne zadania, aby zarobić złoto. 
Każde zadanie posiada m.in. poziom trudności, lokalizację oraz wiedźmina, 
który je wykonał. 

Wśród obiorów nagród zarejestrowanych w ciągu ostatnich 5 minut, wykrywaj 
pary `A` i `B` dotyczące tego samego wiedźmina, o tego samego poziomu trudności 
i tej samej lokalizacji. 
Aby uniknąć duplikatów par `A-B` oraz `B-A`, łącz ze sobą te odbiory nagród, w których 
rejestracja odbioru `A` miała miejsce przed rejestracją odbioru `B` 
(zakładamy, że w tym samym momencie odbiory nagród przez tego samego 
wiedźmina nie występują).

Wyniki powinny zawierać, następujące kolumny:
- `witcher` - nazwa wiedźmina
- `difficulty_level` - poziom trudności zadania
- `location` - lokalizacja zadania
- `difference` - różnica w zapłacie otrzymanej za zadania (`A.payment-B.payment`)

## Zadanie 5

Rejestracja odbioru nagród przez tego samego wiedźmina 
obejmująca co najmniej dwa zadania o poziomie 
trudności `Very Hard` w okresie nie dłuższym niż 10 sekund
(czas pomiędzy rejestracją pierwszego i ostatniego zdarzenia) 
tworzy fazę Wiedźmina *doświadczonego*.
Za *odważnego* Wiedźmina uważamy takiego, który zarejestrował 
odbiór nagród za zadanie o poziomie trudności `Lethal` za 
więcej niż 25 sztuk złota.

Odszukuj wiedźminów, którzy w czasie trwania fazy wiedźmina 
*doświadczonego* zarejestrowali zadanie wiedźmina *odważnego*.

Wyniki powinny zawierać, następujące kolumny:
- `witcher` - nazwa wiedźmina
- `start_its` - czas rejestracji pierwszego z zadań fazy wiedźmina *doświadczonego*
- `end_its` - czas rejestracji ostatniego z zadań fazy wiedźmina *doświadczonego*
- `brave_its` - czas rejestracji zadania wiedźmina *odważnego*

## Zadanie 6

Znajduj przypadki, trzech kolejnych rejestracji odbiorów nagród za 
zadania o poziomie trudności `Hard` na tego samego potwora, 
bez ograniczenia czasowego pomiędzy rejestracjami. 
Raportuj takie trójki zadań, które spełniają następujące warunki:

- *Zadanie 1.* - o wartości co najmniej 50 sztuk złota,
- *Zadanie 2.* - o wartości większej niż wartość w zadaniu 1,
- *Zadanie 3.* - o wartości co najmniej 100 sztuk złota, wykonane przez 
  innego wiedźmina niż w zadaniu 2. 

Wyniki powinny zawierać, następujące kolumny:
- `monster` - nazwa potwora
- `payment0` - zapłata w pierwszym zdarzeniu
- `witcher1` - nazwa wiedźmina w drugim zdarzeniu
- `payment1` - zapłata w drugim zdarzeniu
- `witcher2` - nazwa wiedźmina w trzecim zdarzeniu
- `payment2` - zapłata w trzecim zdarzeniu
- `its2` - moment rejestracji trzeciego zdarzenia

## Zadanie 7

Znajduj przypadki, w których pojedynczy wiedźmin ze szkoły 
Mantikory (`Manticore`) zarejestrował serie co najmniej trzech 
odbiorów nagród za zadania 
o poziomie trudności "Very Hard" o wartości przekraczającej za każdym razem 100 sztuk 
złota oraz każdorazowo o większej wysokości zapłaty niż odbiór poprzedni.  
Seria taka musiała się zakończyć przed zleceniem albo o innej trudności, 
albo o wartości nie większej niż poprzednia.

W wyniku umieszczamy informacje dotyczące pierwszych trzech zdarzeń.

Wyniki powinny zawierać następujące kolumny:
- `witcher` - wiedźmin wykonujący zadania
- `first_payment` - zapłata za pierwsze zadanie
- `second_payment` - zapłata za drugie zadanie
- `third_payment` - zapłata za trzecie zadanie
