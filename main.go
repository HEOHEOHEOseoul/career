package main

import (
	"net/http"

	"github.com/gorilla/mux"
	"github.com/heoseoul/cabb/user/signpkg"
)

func main() {
	mux := mux.NewRouter()
	mux.HandleFunc("/addCareer", signpkg.AddCareer)
	http.ListenAndServe(":80", mux)
}
