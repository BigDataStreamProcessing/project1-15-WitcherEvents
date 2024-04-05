# Charakterystyka danych
Poszczególni wiedźmini z uniwersum *Wiedźmina* wykonują zlecenia na potwory (reprezentując szkołę swojego cechu wiedźmińskiego). W tym celu wykorzystują pomocne eliksiry, które wpływają na poziom zatrucia ich organizmu. Za wykonane zadanie wiedźmini zdobywają zapłatę.

W strumieniu pojawiają się zdarzenia zgodne ze schematem `WitcherEvent`.

```
create json schema WitcherEvent(witcher string, its string, 
  ets string, school string, monster string, payment int, difficulty_level string, 
location string, potion string, poisoning_lvl int);
```

Każde zdarzenie związane z jest z faktem odebrania nagrody (określona ilości złota) przez określonego wiedźmina z określonej szkoły. 

Każde zdarzenie zawiera dodatkowo informacje na temat zabitego potwora, poziom trudności zlecenia (Easy/Medium/Hard/Very Hard/Lethal), lokalizacji zlecenia, a także nazwy użytego eliksiru i poziomu jego toksyczności.

Dane uzupełnione są o etykiety czasowe:
- `ets` - która odzwierciedla moment odbioru zapłaty za zlecenie. Może się on losowo spóźniać w stosunku do czasu systemowego maksymalnie do 1 minuty i jest zaokrąglony do 1 sekundy.
- `its` - czas rejestracji zdarzenia odebrania zapłaty w systemie. Jest on zaokrąglony do 1 sekundy.


# Opis atrybutów
Znaczenie poszczególnych atrybutów w każdym zdarzeniu:

- `witcher` - postać wykonująca zlecenie
- `its` - czas rejestracji zdarzenia w systemie
- `ets` - czas odebrania zapłaty za wykonane zlecenie
- `school` - szkoła cechu wiedźmińskiego (którą reprezentuje wiedźmin wykonujący zlecenie)
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
Obliczaj sumy zarobionego złota przez poszczególne szkoły wiedźmińskie w ciągu ostatniej minuty (czasu systemowego).

Wyniki powinny zawierać, następujące kolumny:
- `school` - nazwa szkoły
- `sumpayment` - suma zarobionego złota przez szkołę wiedźmińską w ciągu ostatniej minuty.

## Zadanie 2
Wykrywaj przypadki, w których otrzymana zapłata przekroczyła 245 sztuk złota.

Wyniki powinny zawierać wszystkie kolumny dotyczące zdarzenia.

## Zadanie 3
Wykrywaj odebrania nagrody za zlecenia, dla których suma poziomów toksyczności eliksirów dla zleceń zarejestrowanych przez tego wiedźmina w ciągu ostatniej pół minuty przekroczyła wartość 20.

Wyniki powinny zawierać, następujące kolumny:
- `witcher` - nazwa wiedźmina
- `total_poisoning` - suma punktów toksyczności eliksirów
- `its` - czas rejestracji odbioru nagrody

## Zadanie 4

Wiedźmini podróżują i wykonują różnorodne zadania, aby zarobić złoto. Każde zadanie posiada m.in. poziom trudności, lokalizację oraz wiedźmina, który je wykonał. 

Wykrywaj pary odbioru nagród, za zlecenia w ciągu ostatnich 5 minut tego samego wiedźmina o tym samym poziomie trudności i w tej samej lokalizacji oraz oblicz różnicę zapłaty otrzymaną za te dwa zadania.

Wyniki powinny zawierać, następujące kolumny:
- `witcher` - nazwa wiedźmina
- `difficulty_level` - poziom trudności zadania
- `location` - lokalizacja zadania
- `difference` - różnica w zapłacie otrzymanej za zadania

## Zadanie 5

Odszukuj wiedźminów, którzy w tym samym czasie mogą być uznani za doświadczonych i odważnych. Za doświadczonego Wiedźmina uważamy takiego, który wykonał serie co najmniej dwóch zadań o poziomie trudności "Very Hard" trwającą nie dłużej niż 10 sekund, a za odważnego, takiego, który wykonał zadanie o poziomie trudności "Lethal". Dodatkowo nagrody za wykonywane zadania mają być większe niż 25 sztuk złota.

Wyniki powinny zawierać, następujące kolumny:
- `witcher` - nazwa wiedźmina
- `ets1` - rozpoczęcie serii zadań wiedźmina doświadczonego
- `ets2` - zakończenie serii zadań wiedźmina doświadczonego

## Zadanie 6

Znajduj przypadki, w wykonane zostały trzy zadania o poziomie trudności "Hard" na tego samego potwora. Dodatkowo spełnione muszą być następujące warunki:

Zadanie 1. - o wartości co najmniej 50 sztuk złota,
Zadanie 2. - o wartości większej niż wartość w zadaniu 1,
Zadanie 3. - o wartości co najmniej 100 sztuk złota, wykonane przez innego wiedźmina niż w zadaniu 2. 

Wyniki powinny zawierać, następujące kolumny:
- `monster` - nazwa potwora
- `payment0` - zapłata w pierwszym zdarzeniu
- `witcher1` - nazwa wiedźmina w drugim zdarzeniu
- `payment1` - zapłata w drugim zdarzeniu
- `witcher2` - nazwa wiedźmina w trzecim zdarzeniu
- `payment2` - zapłata w trzecim zdarzeniu
- `its2` - moment rejestracji trzeciego zdarzenia

## Zadanie 7

Znajduj przypadki, w których pojedynczy wiedźmin ze szkoły Mantikory (`Manticore`) wykonał serie co najmniej trzech zadań o poziomie trudności "Very Hard" i coraz większej wysokości zapłaty każdorazowo przekraczającej 100 sztuk złota. Seria taka musiała się zakończyć albo zleceniem o innej trudności, albo o wartości mniejszej niż poprzednia.

Wyniki powinny zawierać następujące kolumny:
- `witcher` - wiedźmin wykonujący zadania
- `first_payment` - zapłata za pierwsze zadanie
- `second_payment` - zapłata za drugie zadanie
- `third_payment` - zapłata za trzecie zadanie
