package main

import (
	"fmt"
	"log"

	amqp "github.com/rabbitmq/amqp091-go"
)

func failOnError(err error, msg string) {
	if err != nil {
		log.Fatalf("%s: %s", msg, err)
	}
}

func main() {
	conn, err := amqp.Dial("amqp://guest:guest@localhost:5672/")
	failOnError(err, "No se pudo conectar a RabbitMQ")
	defer conn.Close()

	ch, err := conn.Channel()
	failOnError(err, "No se pudo abrir un canal")
	defer ch.Close()

	q, err := ch.QueueDeclare(
		"pedidos_cocina",
		true,
		false,
		false,
		false,
		nil,
	)
	failOnError(err, "No se pudo declarar la cola")

	msgs, err := ch.Consume(
		q.Name,
		"",
		true,
		false,
		false,
		false,
		nil,
	)
	failOnError(err, "No se pudo registrar el consumidor")

	fmt.Println("[COCINA] Esperando pedidos...")

	for d := range msgs {
		fmt.Printf("[COCINA] Preparando -> %s\n", d.Body)
	}
}