package taxipark

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> {
    println("finding fake drivers...")

    // this.allDrivers.filter { d -> trips.none { it.driver == d } }.toSet()

    return this.allDrivers
        .map { driver -> driver.name }
        .filter { driverName -> driverName !in this.trips.map { t -> t.driver.name } }
        .map { driverName -> Driver(driverName) }
        .toSet()

}

/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> {
    if (minTrips == 0) {
        return this.allPassengers
    }

    val passengerTrips = mutableMapOf<String, Int>()

    this.trips
        .flatMap { trip -> trip.passengers }
        .forEach { passenger ->
            if (passengerTrips.containsKey(passenger.name)) {
                passengerTrips.merge(passenger.name, 1, Int::plus)
            } else {
                passengerTrips[passenger.name] = 1
            }
        }

    val faithfulPassengers = passengerTrips.filterValues { value -> value >= minTrips }

    return faithfulPassengers.map { (passengerName, _) -> Passenger(passengerName) }.toSet()
}

/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> {
    val passengerTrips = mutableMapOf<String, Int>()

    this.trips
        .filter { trip -> trip.driver.name == driver.name }
        .flatMap { trip -> trip.passengers }
        .forEach { passenger ->
            if (passengerTrips.containsKey(passenger.name)) {
                passengerTrips.merge(passenger.name, 1, Int::plus)
            } else {
                passengerTrips[passenger.name] = 1
            }
        }

    val frequentPassengers = passengerTrips.filterValues { tripsQuantity -> tripsQuantity > 1 }

    return frequentPassengers.map { (passengerName, _) -> Passenger(passengerName) }.toSet()
}

/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> {
    /*val (tripsWithDiscount, tripsWithoutDiscount) = trips.partition { it.discount != null }
    return allPassengers.filter { passenger ->
        tripsWithDiscount.count { passenger in it.passengers }  >
                tripsWithoutDiscount.count{ passenger in it.passengers }
    }.toSet()*/

    val passengerTripsWithDiscount = mutableMapOf<String, Int>()
    val passengerTripsWithoutDiscount = mutableMapOf<String, Int>()

    val tripsDividedByDiscount = this.trips.partition { trip -> trip.discount != null }

    /**
     * fill the map with the count of total trips of each passenger
     */
    fun fillMap(mapToFill: MutableMap<String, Int>, trips: List<Trip>) {
        trips
            .flatMap { trip -> trip.passengers }
            .forEach { passenger ->
                if (mapToFill.containsKey(passenger.name)) {
                    mapToFill.merge(passenger.name, 1, Int::plus)
                } else {
                    mapToFill[passenger.name] = 1
                }
            }
    }

    /**
     * checks if passenger is considered smart or not
     */
    fun isItSmart(passengerName: String, totalOfTripsWithDiscount: Int): Boolean {
        return if (passengerTripsWithoutDiscount.containsKey(passengerName)) {
            totalOfTripsWithDiscount > passengerTripsWithoutDiscount[passengerName]!!
        } else {
            // si el pasajero nunca viajo sin descuento peor tuvo al menos un viaje con descuento
            true
        }
    }

    // trips with discount
    fillMap(passengerTripsWithDiscount, tripsDividedByDiscount.first)

    // trips without discount
    fillMap(passengerTripsWithoutDiscount, tripsDividedByDiscount.second)

    // filter only passengers with more discounted trips than trips without discount
    val smartPassengers = passengerTripsWithDiscount.filter { (passengerName, totalOfTripsWithDiscount) ->
        isItSmart(passengerName, totalOfTripsWithDiscount)
    }

    return smartPassengers.map { (passengerName, _) -> Passenger(passengerName) }.toSet()
}

/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {
    val maxDurationTrip: Trip? = this.trips.maxByOrNull { it.duration }

    val totalChunks: Int
    if (maxDurationTrip != null) {
        totalChunks = maxDurationTrip.duration / 10 + 1
    } else {
        // no hay nada que hacer si no hay una duracion maxima disponible
        return null
    }

    val mapOfRanges: MutableMap<IntRange, Int> = mutableMapOf()
    var auxRange: IntRange = 0..9
    for (i in 1..totalChunks) {
        if (i == 1)
            mapOfRanges[auxRange] = 0
        else {
            auxRange = auxRange.first + 10..auxRange.last + 10
            mapOfRanges[auxRange] = 0
        }
    }

    this.trips
        .map { it.duration }
        .forEach { duration ->
            mapOfRanges.onEach { entry ->
                if (duration in entry.key) {
                    mapOfRanges.merge(entry.key, 1, Int::plus)
                }
            }
        }


    return mapOfRanges.maxByOrNull { it.value }?.key
}

/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    if (this.trips.isNullOrEmpty())
        return false

    val totalDrivers = this.allDrivers.count()

    val totalIncome = this.trips.map { it.cost }.reduce { sum, cost -> sum + cost }

    val twentyPercentOfDrivers = (totalDrivers * 0.2).toInt()

    val eightyPercentOfTotalIncome = (totalIncome * 0.8)

    val driversAndTotalIncome: MutableMap<String, Double> = mutableMapOf()

    this.trips
        .forEach { trip ->
            if (driversAndTotalIncome.containsKey(trip.driver.name)) {
                driversAndTotalIncome.merge(trip.driver.name, trip.cost, Double::plus)
            } else {
                driversAndTotalIncome[trip.driver.name] = trip.cost
            }
        }

    val top20PercentBestDrivers = driversAndTotalIncome.toList()
        .sortedByDescending { (_, value) -> value }
        .take(twentyPercentOfDrivers)


    return top20PercentBestDrivers
        .map { (_, totalDriverIncome) -> totalDriverIncome }
        .reduce { sum, totalDriverIncome -> sum + totalDriverIncome } >= eightyPercentOfTotalIncome
}

fun main(args: Array<String>) {
    val t = TaxiPark(setOf(), setOf(), listOf())

    t.checkParetoPrinciple()

}