package main

import (
	"context"
	"fmt"
	"log"
	"time"

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

	pedidos := []string{
		"Pedido #1: 2 pizzas grandes - Barrio San Roque",
		"Pedido #2: 1 pizza mediana + gaseosa - Av. Hernando Siles",
		"Pedido #3: 3 pizzas familiares - Recoleta",
	}

	for _, pedido := range pedidos {
		ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)

		err = ch.PublishWithContext(
			ctx,
			"",
			q.Name,
			false,
			false,
			amqp.Publishing{
				ContentType: "text/plain",
				Body:        []byte(pedido),
			},
		)

		cancel()

		failOnError(err, "No se pudo enviar el pedido")

		fmt.Printf("[PIZZERIA] Enviado -> %s\n", pedido)

		time.Sleep(2 * time.Second)
	}
}