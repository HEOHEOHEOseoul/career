package main

// go version  go 1.18.4 window/amd64
//Restful API

import (
	"bytes"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"net/rpc"

	"github.com/gorilla/mux"
)

type Args struct {
	Alias   string
	Address string
}

// Request Alias != nil -> Wallet 생성

// Request Tx != nil -> 블록 생성

// Request Tx == nil && Address != nil -> 블록 조회

type Request struct {
	Alias   string
	Address string
	T       *Transaction
}

type Transaction struct {
	Txid      []byte // 거래 ID
	TimeStamp []byte // 거래시간
	Applier   []byte // 요청자
	Company   []byte // 경력회사
	Career    []byte // 경력기간
	Payment   []byte // 결제수단
	Job       []byte // 직종 , 업무
	Proof     []byte // pdf 링크
}

type Response struct {
	Address    string
	PublicKey  []byte
	PrivateKey []byte
	Check      bool
}

// WBS ( Work Based Schedule ) by Excel
// QS (Quality of Service ) by Excel
func main() {
	router := mux.NewRouter()
	r := &Request{}
	// r =
	// if /mdware/main으로 요청이 들어오면 r.ConnectWallet 실행
	router.HandleFunc("/MakeWallet", r.ConnectWallet)
	// if /mdware/Tx으로 요청이 들어오면 r.ConnectTransaction 실행
	router.HandleFunc("/CheckAddress", r.CheckAddress)
	// if /mdware/FindTxbyAddr으로 요청이 들어오면 r.ConnectTransaction 실행

	log.Fatal(http.ListenAndServe(":3000", router))
	// Request 가 가지고 있는 T를 임의로 넣기 ( 테스트를 위해서)
}
func (r *Request) ConnectWallet(w http.ResponseWriter, re *http.Request) {
	//------------ Json 으로 들어온 Alias 확인 ( 서버에서 Send)
	headerContentType := re.Header.Get("Content-Type")
	if headerContentType != "application/json" {
		// json 타입이 아니라면
		fmt.Println("Json 타입이 아닙니다!!")
	}
	decoder := json.NewDecoder(re.Body)
	var request Request
	err := decoder.Decode(&request) // request Body에 들어있는 json 데이터를 해독하고 저장
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Println(request.Alias, "요청받은 Alias 입니다.")

	// -----------------------------------------JSON 해독 끝 -------------------------

	// ------------------------- RPC 서버 연결 ---------------------
	Client, err := rpc.Dial("tcp", "127.0.0.1:9000")
	if err != nil {
		fmt.Println(err)
		return
	}
	defer Client.Close()
	response := new(Response) // 연결후 return을 받기 위해 빈 바구니 생성
	err = Client.Call("RpcServer.MakeNewWallet", request.Alias, response)
	if err != nil {
		fmt.Println(err, "                                             Client.Call 에서 에러가 났음 ")
		return
	}

	fmt.Println(request.Alias, "님의 지갑의 Address 입니다 ", response.Address, response.PrivateKey, "님의 PrivateKey 입니다. 보안에 유의하세요", response.PublicKey, "님의 PublicKey 입니다")
	// Wallet.go에서 받아온 데이터 요청한 서비스로 다시 돌려주기
	// 돌려주기 위해서 Json Parsing
	PrivateKey := hex.EncodeToString(response.PrivateKey)
	PublicKey := hex.EncodeToString(response.PublicKey)
	fmt.Println(PrivateKey, "PrvateKey")
	fmt.Println(PublicKey, "PublicKey")
	value := map[string]interface{}{
		"Alias":      r.Alias,
		"Address":    response.Address,
		"PublicKey":  PublicKey,
		"PrivateKey": PrivateKey,
	}

	json_data, err := json.Marshal(value) // Parsing 완료
	fmt.Println(json_data, "json 파싱한 후 데이터 ")

	// 보내주고 res 받기 (true or false for Packet loss )
	// res, err := http.Post("http://localhost:3000/mypage/wallet", "application/json", bytes.NewBuffer(json_data))
	// if err != nil {
	// 	fmt.Println(err)
	// 	return
	// }
	// fmt.Println(res)
}
func (r *Request) CheckAddress(w http.ResponseWriter, re *http.Request) {
	// 주소 검증.
	headerContentType := re.Header.Get("Content-Type")
	if headerContentType != "application/json" {
		// json 타입이 아니라면
		fmt.Println("Json 타입이 아닙니다!!")
	}
	decoder := json.NewDecoder(re.Body)
	var request Request
	err := decoder.Decode(&request) // request Body에 들어있는 json 데이터를 해독하고 저장
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Println(request.Address, "요청받은 Address 입니다.")
	Client, err := rpc.Dial("tcp", "127.0.0.1:9000")

	if err != nil {
		fmt.Println(err)
		return
	}
	defer Client.Close()
	response := new(Response)
	err = Client.Call("RpcServer.CheckAddress", request.Address, response)
	if err != nil {
		fmt.Println(err)
		return
	}
	if response.Check {
		fmt.Println("존재하는 지갑주소입니다")
		/*value := map[string]interface{}{
			"Alias":      r.Alias,
			"Address":    response.Address,
			"PublicKey":  PublicKey,
			"PrivateKey": PrivateKey,
		}*/
		value := map[string]interface{}{
			"Address": request.Address,
		}
		json_data, err := json.Marshal(value)
		http.Post("http://localhost:3000/FindAllTxByAddress", "application/json", bytes.NewBuffer(json_data))
		if err != nil {
			fmt.Println(err)
			return
		}
	} else {
		fmt.Println("존재하지 않는 지갑주소입니다.")
	}
}

// 지갑 주소를 주고 그 주소에 해당하는 지갑을 받아오기
func (r *Request) GetWallet(w http.ResponseWriter, re *http.Request) {
	headerContentType := re.Header.Get("Content-Type")
	if headerContentType != "application/json" {
		// json 타입이 아니라면
		fmt.Println("Json 타입이 아닙니다!!")
	}
	decoder := json.NewDecoder(re.Body)
	var request Request
	err := decoder.Decode(&request) // request Body에 들어있는 json 데이터를 해독하고 저장
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Println(request.Address, "요청받은 Address 입니다.")
	Client, err := rpc.Dial("tcp", "127.0.0.1:9000")
	if err != nil {
		fmt.Println(err)
		return
	}
	defer Client.Close()
	response := new(Response)
	err = Client.Call("RpcServer.GetWallet", request.Address, response)
	if err != nil {
		fmt.Println(err)
		return
	}
}
